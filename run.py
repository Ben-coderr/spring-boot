import subprocess
import time
import sys
from datetime import datetime

# Color codes
COLOR = {
    "HEADER": "\033[95m",
    "BLUE": "\033[94m",
    "CYAN": "\033[96m",
    "GREEN": "\033[92m",
    "YELLOW": "\033[93m",
    "RED": "\033[91m",
    "RESET": "\033[0m"
}

# Configuration
COMPOSE_FILE = "docker-compose.yml"
MAVEN_CMD = "mvn"
CONTAINER_NAME = "school-postgres"  # For docker exec/logs
SERVICE_NAME = "postgres"          # For docker-compose commands
DB_USER = "admin"
DB_NAME = "schooldb"

def print_status(emoji, color, step, message):
    timestamp = datetime.now().strftime("%H:%M:%S")
    print(f"{color}{emoji} [{timestamp}] {step}: {message}{COLOR['RESET']}")

def run_command(command, step):
    try:
        result = subprocess.run(
            command,
            shell=True,
            check=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        print_status("✅", COLOR["GREEN"], step, "Completed successfully")
        return True
    except subprocess.CalledProcessError as e:
        print_status("❌", COLOR["RED"], step, "Failed")
        print(f"Command: {e.cmd}")
        print(f"Error: {e.stderr}")
        sys.exit(1)

def cleanup_environment():
    print_status("🧹", COLOR["YELLOW"], "CLEANUP", "Removing existing containers")
    run_command(f"docker-compose -f {COMPOSE_FILE} down -v", "Docker Cleanup")

def build_project():
    print_status("🔨", COLOR["CYAN"], "BUILD", "Building project with Maven")
    run_command(f"{MAVEN_CMD} clean package -DskipTests", "Maven Build")

def start_infrastructure():
    print_status("🐘", COLOR["BLUE"], "INFRASTRUCTURE", "Starting PostgreSQL")
    run_command(f"docker-compose -f {COMPOSE_FILE} up -d {SERVICE_NAME}", "Docker Start")

def wait_for_postgres():
    print_status("⏳", COLOR["CYAN"], "DATABASE", "Waiting for PostgreSQL readiness")
    spinner = ["⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"]
    timeout = 60
    start_time = time.time()
    attempts = 0

    while time.time() - start_time < timeout:
        attempts += 1
        result = subprocess.run(
            f"docker exec {CONTAINER_NAME} pg_isready -U {DB_USER} -d {DB_NAME}",
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )
        if result.returncode == 0:
            print_status("✅", COLOR["GREEN"], "DATABASE", f"Ready after {attempts} attempts")
            return
        sys.stdout.write(f"\r{spinner[attempts % 10]} Attempt {attempts}...")
        sys.stdout.flush()
        time.sleep(1)

    print_status("❌", COLOR["RED"], "DATABASE", f"PostgreSQL not ready after {timeout}s")
    print("Last check output:", result.stdout.decode(), result.stderr.decode())
    print("\nChecking container logs...")
    logs = subprocess.run(
        f"docker logs {CONTAINER_NAME}",
        shell=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )
    print(logs.stdout.decode())
    sys.exit(1)

def start_application():
    print_status("🚀", COLOR["HEADER"], "APPLICATION", "Starting Spring Boot")
    try:
        subprocess.run(
            f"{MAVEN_CMD} spring-boot:run",
            shell=True,
            check=True
        )
    except subprocess.CalledProcessError as e:
        print_status("💥", COLOR["RED"], "APPLICATION", "Application crashed")
        sys.exit(1)

def main():
    try:
        print(f"\n{COLOR['HEADER']}🏫 School Management System Startup{COLOR['RESET']}")
        cleanup_environment()
        build_project()
        start_infrastructure()
        wait_for_postgres()
        start_application()
    except KeyboardInterrupt:
        print_status("🛑", COLOR["YELLOW"], "SYSTEM", "Process interrupted by user")
        cleanup_environment()
        sys.exit(0)

if __name__ == "__main__":
    main()