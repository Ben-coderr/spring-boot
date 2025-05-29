#!/usr/bin/env python3
"""
Seed + edge-case-test harness for the Edusphere API.

Exit codes
  0 – all tests pass
  1 – at least one test failed

Dependencies
  pip install requests faker tqdm tabulate
"""
from __future__ import annotations

import argparse, random, string, sys
from typing import Dict, List

import requests
from faker import Faker
from tabulate import tabulate
from tqdm import tqdm


# ─── helpers ──────────────────────────────────────────────────────────────
fake = Faker()
rand = random.Random(42)


def _rand_phone() -> str:     # 12-digit
    return fake.msisdn()[:12]


def _rand_blood() -> str:
    return rand.choice("A+ A- B+ B- AB+ AB- O+ O-".split())


def _unique(uname: str, seen: set[str]) -> str:
    if uname not in seen:
        seen.add(uname)
        return uname
    base = uname.split("@")[0]
    while True:
        cand = f"{base}-{''.join(rand.choices(string.ascii_lowercase+string.digits,k=5))}"
        if cand not in seen:
            seen.add(cand); return cand


def _post(sess: requests.Session, url: str, payload: dict, ok=(200, 201)):
    r = sess.post(url, json=payload)
    if r.status_code not in ok:
        raise RuntimeError(f"POST {url} → {r.status_code}: {r.text[:300]}")
    return {} if not r.text.strip() else r.json()


# ─── seeding ──────────────────────────────────────────────────────────────
def create_grades(s, host) -> Dict[int, int]:
    return {lvl: _post(s, f"{host}/grades", {"level": lvl})["id"] for lvl in range(1, 13)}


def create_subjects(s, host):
    names = "Math Physics Chemistry Biology History Geography English French Computer-Science Arts".split()
    def dto(n): return {"name": n, "ccWeight": 40, "examWeight": 50, "attendanceWeight": 10}
    return {n: _post(s, f"{host}/subjects", dto(n))["id"] for n in names}


def create_parents(s, host, n, seen) -> List[int]:
    out = []
    for _ in tqdm(range(n), desc="parents"):
        mail = fake.email()
        out.append(_post(s, f"{host}/parents", {
            "fullName": fake.name(),
            "phone": _rand_phone(),
            "email": mail,
            "user": {"username": _unique(mail, seen), "password": "pwd"}
        })["id"])
    return out


def create_teachers(s, host, subs, n, seen):
    ids, sub_ids = [], list(subs.values())
    for _ in tqdm(range(n), desc="teachers"):
        mail = fake.email()
        ids.append(_post(s, f"{host}/teachers", {
            "fullName": fake.name(),
            "email": mail,
            "phone": _rand_phone(),
            "subject": {"id": rand.choice(sub_ids)},
            "bloodType": _rand_blood(),
            "sex": rand.choice(["MALE", "FEMALE"]),
            "user": {"username": _unique(mail, seen), "password": "pwd"}
        })["id"])
    return ids


def create_classes(s, host, grades, teachers, per_grade):
    ids = []
    for lvl, gid in grades.items():
        for idx in range(per_grade):
            ids.append(_post(s, f"{host}/classes", {
                "name": f"{lvl}{chr(65+idx)}",
                "capacity": 2 if idx == 0 else 30,      # tiny class for “full” test
                "grade": {"id": gid},
                "supervisor": {"id": rand.choice(teachers)}
            })["id"])
    return ids


def create_students(s, host, classes, n, seen):
    ids = []
    for _ in tqdm(range(n), desc="students"):
        mail = fake.email()
        ids.append(_post(s, f"{host}/students", {
            "fullName": fake.name(),
            "email": mail,
            "schoolClass": {"id": rand.choice(classes)},
            "user": {"username": _unique(mail, seen), "password": "pwd"}
        })["id"])
    return ids


def create_lessons(s, host, subs, teachers, classes):
    ids = []
    for cls in tqdm(classes, desc="lessons"):
        ids.append(_post(s, f"{host}/lessons", {
            "topic": fake.word().title(),
            "lessonDate": str(fake.date_between('-5d', '+5d')),
            "day": "MONDAY",
            "startTime": "08:00:00",
            "endTime": "09:00:00",
            "subject": {"id": rand.choice(list(subs.values()))},
            "teacher": {"id": rand.choice(teachers)},
            "schoolClass": {"id": cls}
        })["id"])
    return ids


def create_exams(s, host, lessons):
    return [_post(s, f"{host}/exams", {
        "title": "Mid-term",
        "examDate": str(fake.date_between('-2d', '+20d')),
        "lesson": {"id": lid}
    })["id"] for lid in lessons[:20]]


def seed_database(sess, host):
    seen: set[str] = set()
    grades   = create_grades(sess, host)
    subjects = create_subjects(sess, host)
    teachers = create_teachers(sess, host, subjects, 6, seen)
    classes  = create_classes(sess, host, grades, teachers, 1)
    students = create_students(sess, host, classes, 25, seen)
    lessons  = create_lessons(sess, host, subjects, teachers, classes)
    exams    = create_exams(sess, host, lessons)
    return dict(grades=grades, subjects=subjects, teachers=teachers,
                classes=classes, students=students, lessons=lessons, exams=exams)


# ─── test-runner ──────────────────────────────────────────────────────────
class TestRunner:
    def __init__(self, sess: requests.Session, host: str, data):
        self.s, self.host, self.d = sess, host.rstrip("/"), data
        self.results: list[tuple[str,bool,str]] = []

    # flexible status checker ------------------------------------------------
    @staticmethod
    def _ok(got: int, expect) -> bool:
        if expect is None:                      # “any 2xx”
            return 200 <= got < 300
        if isinstance(expect, int):
            expect = (expect,)
        return got in expect

    def _rec(self, label: str, ok: bool, code: int):
        self.results.append((label, ok, str(code)))

    # HTTP helpers -----------------------------------------------------------
    def _get(self, path, expect=200):
        r = self.s.get(self.host + path)
        self._rec(f"GET  {path}", self._ok(r.status_code, expect), r.status_code)
        return r

    def _post(self, path, body, expect=200):
        r = self.s.post(self.host + path, json=body)
        self._rec(f"POST {path}", self._ok(r.status_code, expect), r.status_code)
        return r

    def _put(self, path, body, expect=200):
        r = self.s.put(self.host + path, json=body)
        self._rec(f"PUT  {path}", self._ok(r.status_code, expect), r.status_code)
        return r

    # tests ------------------------------------------------------------------
    def run(self):
        sid      = self.d["students"][0]
        cid_full = self.d["classes"][0]          # capacity 2
        lid      = self.d["lessons"][0]
        eid      = self.d["exams"][0]

        # 1. auth required
        r = requests.get(f"{self.host}/grades")
        self._rec("GET  /grades (no auth)", r.status_code==401, r.status_code)

        # 2. duplicate grade level (400/403/409 all acceptable)
        self._post("/grades", {"level": 1}, expect=(400,403,409))

        # 3. subject without name (400/422)
        self._post("/subjects", {"coefficient":3}, expect=(400,422))

        # 4. student without password (400)
        self._post("/students", {"fullName":"X"}, expect=400)

        # 5. bad attendance status
        self._post("/attendances", {
            "status":"SLEEPING","date":"2025-01-01",
            "student":{"id":sid},"lesson":{"id":lid}}, expect=400)

        # 6. result missing kind
        self._post("/results", {
            "score":10,"student":{"id":sid},"exam":{"id":eid}}, expect=400)

        # 7. move to full class – first fill it
        st1, st2 = self.d["students"][1:3]
        self._put(f"/students/{st1}/class/{cid_full}", {}, expect=200)
        self._put(f"/students/{st2}/class/{cid_full}", {}, expect=200)
        self._put(f"/students/{sid}/class/{cid_full}", {}, expect=(400,409))

        # 8. lesson w/out teacher
        self._post("/lessons", {
            "topic":"Bad","subject":{"id":list(self.d["subjects"].values())[0]},
            "schoolClass":{"id":cid_full}}, expect=400)

        # 9. missing resource
        self._get("/teachers/999999", expect=404)

        # 10. unapproved login
        new_mail = fake.email()
        self._post("/auth/signup/teacher", {
            "username":new_mail,"password":"pwd","fullName":"Tmp",
            "email":new_mail}, expect=201)
        r = self.s.post(self.host+"/auth/login",
                        json={"username":new_mail,"password":"pwd"})
        self._rec("POST /auth/login (unapproved)", r.status_code==403, r.status_code)

        # 11. approve then login
        uid = self._last_unapproved_id()
        self._post(f"/admins/approve/{uid}", {}, expect=(200,204))
        r2 = self.s.post(self.host+"/auth/login",
                         json={"username":new_mail,"password":"pwd"})
        self._rec("POST /auth/login (approved)", r2.status_code==200, r2.status_code)

        # 12. dashboard
        self._get("/dash/occupancy", expect=200)

    def _last_unapproved_id(self):
        r = self.s.get(self.host+"/admins/unapproved"); r.raise_for_status()
        return r.json()[-1]["id"]

    # report -----------------------------------------------------------------
    def report(self):
        print(tabulate([(n,"✅" if ok else "❌",s) for n,ok,s in self.results],
                       headers=("Test","OK","status"),tablefmt="github"))
        fails = [t for t in self.results if not t[1]]
        print("\n✅  All tests passed." if not fails else
              f"\n❌  {len(fails)} / {len(self.results)} failed.")
        return not fails


# ─── main ─────────────────────────────────────────────────────────────────
def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--host", default="http://localhost:8083")
    ap.add_argument("--user", default="admin")
    ap.add_argument("--password", default="password")
    args = ap.parse_args()

    sess = requests.Session()
    r = sess.post(f"{args.host}/auth/login",
                  json={"username":args.user,"password":args.password})
    try:
        r.raise_for_status()
    except Exception:
        sys.exit(f"Auth failed: {r.status_code} {r.text}")
    sess.headers["Authorization"] = f"Bearer {r.json()['token']}"

    print("🌱  Seeding database …"); data = seed_database(sess, args.host); print("✅  Seeded.")
    print("\n🧪  Running edge-case tests …")
    ok = TestRunner(sess, args.host, data).run() or True  # run returns None
    tr = TestRunner(sess,args.host,data); tr.run(); success = tr.report()
    sys.exit(0 if success else 1)


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        sys.exit("\nInterrupted.")
