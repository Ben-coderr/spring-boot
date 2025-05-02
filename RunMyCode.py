#!/usr/bin/env python3
"""
RunMyCode.py – one-click dev bootstrap

 • prints OS / Python
 • checks Docker *daemon* is up
 • ensures a clean MySQL 8.0 on localhost:3307  (root/secret)
 • (re)creates schema  `schooldb`
 • launches the Spring-Boot app (mvn clean spring-boot:run)
 • waits until the app is **running** then inserts a Super-Admin row
"""

from __future__ import annotations
import os, platform, re, shutil, subprocess as sp, sys, time
from pathlib import Path
from urllib.request import urlopen
from urllib.error   import URLError

CONTAINER   = "school-mysql-8"
ROOT_PW     = "secret"
PORT        = "3307"          # host → container:3306
APP_PORT    = 8082            # Spring
ADMIN_EMAIL = "admin@example.com"
ADMIN_PW_BCRYPT = (
    "$2a$10$MtDv8oylmbWc9LzB/k3jYuz1kAkdNhkw1h1lOsuF9GIBKMsOF1iSm"
)  # raw: pa55w0rd

# ─── shell helpers ────────────────────────────────────────────────────────
def sh(cmd: list[str] | str, *, capture=False, check=True, env=None) -> str | None:
    if isinstance(cmd, str):
        cmd = cmd.split()
    try:
        if capture:
            return sp.check_output(cmd, text=True, env=env)
        sp.run(cmd, check=check, env=env)
    except sp.CalledProcessError as err:
        if capture and err.output:
            print(err.output, file=sys.stderr)
        raise

def docker(*args: str, capture=False) -> str | None:
    return sh(["docker", *args], capture=capture)

def header(k: str, v: str) -> None:
    print(f"🏷️  {k:<10}: {v}")

# ─── validations ─────────────────────────────────────────────────────────
def assert_docker_running() -> None:
    """Fail early if Docker socket/daemon is unavailable."""
    try:
        docker("info", "--format", "'{{json .ServerVersion}}'", capture=True)
    except (FileNotFoundError, sp.CalledProcessError):
        sys.exit("❌  Docker daemon not running (or CLI not installed). Please start Docker Desktop / service.")

def port_in_use() -> str | None:
    """Return container-ID already bound to host-port 3307, else None."""
    out = docker("ps", "--format", "{{.ID}}\t{{.Ports}}", capture=True)
    for line in out.strip().splitlines():
        cid, ports = line.split("\t", 1)
        if re.search(fr"(0\.0\.0\.0|\:\:):{PORT}->3306", ports):
            return cid
    return None

# ─── MySQL orchestration ────────────────────────────────────────────────
def ensure_fresh_mysql() -> str:
    """Guarantee a fresh MySQL container and return its ID."""
    # delete stale container with our *name*
    try:
        docker("inspect", CONTAINER, capture=True)
        print(f"ℹ️   Removing previous container '{CONTAINER}' …")
        docker("rm", "-f", CONTAINER)
    except sp.CalledProcessError:
        pass

    culprit = port_in_use()
    if culprit:
        img = docker("inspect", "-f", "{{.Config.Image}}", culprit, capture=True).strip()
        print(
            f"⚠️  Port {PORT} already used by container {culprit[:12]} (image: {img}).\n"
            "   Deleting it may erase important data that lives in that container."
        )
        if input("   ➜  Remove it and continue? [y/N] ").strip().lower() != "y":
            sys.exit("🚫  Aborted – existing container kept intact.")
        print("🗑️   Removing conflicting container …")
        docker("rm", "-f", culprit)

    print("🚢  Launching MySQL 8.0 container …")
    cid = docker(
        "run", "-d",
        "--name", CONTAINER,
        "-e", f"MYSQL_ROOT_PASSWORD={ROOT_PW}",
        "-p", f"{PORT}:3306",
        "mysql:8.0",
        capture=True
    ).strip()

    # wait until MySQL is ready
    print("⏳  Waiting for MySQL to accept connections", end="", flush=True)
    for _ in range(60):
        try:
            docker("exec", "-i", cid,
                   "mysql", "-uroot", f"-p{ROOT_PW}", "-e", "SELECT 1;")
            print(" ✅")
            return cid
        except sp.CalledProcessError:
            time.sleep(1)
            print(".", end="", flush=True)
    sys.exit("\n❌  MySQL did not start within 60 s – aborting.")

def reset_schema(cid: str) -> None:
    print("🔄  Re-creating schema `schooldb` …")
    docker("exec", "-i", cid,
           "mysql", "-uroot", f"-p{ROOT_PW}",
           "-e", (
               "DROP DATABASE IF EXISTS schooldb;"
               "CREATE DATABASE schooldb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
           ))

def insert_admin(cid: str) -> None:
    print("➕  Inserting Super-Admin row …")
    docker("exec", "-i", cid,
           "mysql", "-uroot", f"-p{ROOT_PW}", "schooldb",
           "-e", (
               "INSERT INTO admin (full_name,email,password,role) VALUES "
               f"('Super Admin','{ADMIN_EMAIL}','{ADMIN_PW_BCRYPT}','ADMIN') "
               "ON DUPLICATE KEY UPDATE password=VALUES(password);"
           ))

# ─── Spring-Boot launcher ───────────────────────────────────────────────
def wait_for_app() -> None:
    """Poll http://localhost:8082 until reachable (max 90 s)."""
    url = f"http://localhost:{APP_PORT}"
    for _ in range(90):
        try:
            urlopen(url, timeout=2).read(1)
            return
        except URLError:
            time.sleep(1)

def run_spring_boot(cid: str) -> None:
    print("\n🚀  Building & starting Spring-Boot application …\n")

    mvn = (Path(".") / "mvnw").as_posix() if Path("mvnw").exists() else shutil.which("mvn") or shutil.which("mvn.cmd")
    if not mvn:
        sys.exit("❌  Maven not found – install it or add the Maven Wrapper (mvnw).")

    proc = sp.Popen(
        [mvn, "clean", "spring-boot:run"],
        stdout=sp.PIPE,
        stderr=sp.STDOUT,
        text=True,
        bufsize=1,
        universal_newlines=True
    )

    admin_done = False
    try:
        for raw in iter(proc.stdout.readline, ''):        
            line = raw.rstrip('\n')
            print(line)
            if not admin_done and (
                "Tomcat started on port" in line
                or "Started SchoolManagementApplication" in line
            ):
                # wait_for_app()
                insert_admin(cid)
                admin_done = True
                print(
                    "\n✅  Super-Admin ready  →  http://localhost:8082"
                    f"\n   email : {ADMIN_EMAIL}"
                    "\n   pass  : pa55w0rd\n"
                )
        proc.wait()
        if proc.returncode != 0:
            sys.exit(f"❌  Spring-Boot terminated with exit-code {proc.returncode}.")
    except KeyboardInterrupt:
        print("\n⏹️  CTRL-C – stopping Spring-Boot …")
        proc.terminate()
        proc.wait(10)
    finally:
        if not admin_done:
            try:
                insert_admin(cid)
            except Exception:
                pass   # ignore if MySQL/App gone

# ─── MAIN ────────────────────────────────────────────────────────────────
def main() -> None:
    header("OS",      f"{platform.system()} {platform.release()} ({platform.version()})")
    header("Python",  platform.python_version())

    assert_docker_running()
    cid = ensure_fresh_mysql()
    reset_schema(cid)
    run_spring_boot(cid)

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n⏹️  Aborted by user.")
