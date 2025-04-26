#!/usr/bin/env python3
"""
seed_school.py  –  populate the Spring-Boot School-Management API
USAGE
  $ pip install requests faker tqdm
  $ python seed_school.py --base-url http://localhost:8081/api \
                          --out-dir seed_out
OPTIONS
  --wipe-first        drop & recreate the schema before seeding
                      (expects you to have a helper shell script /
                       docker-exec handy – see README)
The script **only** uses the routes that exist in the current controllers:
 admins, grades, classes, subjects, teachers, students, lessons, exams,
 assignments, results, attendances, events, announcements                """

from __future__ import annotations
import argparse, json, sys, random, itertools
from datetime import date, timedelta, datetime
from pathlib import Path

import requests
from faker import Faker
from tqdm import tqdm

fake = Faker()

# ─────────────────────────────── tweak here ──────────────────────────────
GRADES           = ["Grade 1", "Grade 2", "Grade 3"]
CLASSES_PER_G    = 3
SUBJECTS         = ["Maths", "Science", "English", "History", "Geography"]
TEACHERS         = 15
STUDENTS_PER_C   = 25
LESSONS_PER_SC   = 2
ATTENDANCE_RATE  = .92
# ──────────────────────────────────────────────────────────────────────────

def post(url: str, payload: dict, session: requests.Session, logf):
    r = session.post(url, json=payload)
    try:
        r.raise_for_status()
    except requests.HTTPError:
        print("⇩  ERROR", url, r.text[:200], file=sys.stderr)
        raise
    res = r.json()
    print("⇧  POST", url.replace(session.base_url, ""), "→", res, flush=True)
    logf.write(json.dumps({"url": url, "payload": payload, "response": res}) + "\n")
    return res["id"]

def rand_status() -> str:
    return random.choices(
        ["PRESENT", "ABSENT", "LATE"],
        weights=[ATTENDANCE_RATE, 1-ATTENDANCE_RATE-.02, .02]
    )[0]

def seed(base_url: str, out_dir: Path):

    out_dir.mkdir(parents=True, exist_ok=True)
    log_file = out_dir / "seed_log.ndjson"
    logf = log_file.open("w", encoding="utf-8")

    with requests.Session() as s:
        s.base_url = base_url.rstrip("/")
        # ---------- Admin ----------
        post(f"{s.base_url}/admins", {
            "fullName": "Super Admin",
            "email":    f"root-{datetime.utcnow().timestamp():.0f}@school.test"
        }, s, logf)

        # ---------- Grades ----------
        grade_ids = [post(f"{s.base_url}/grades", {"name": g}, s, logf)
                     for g in GRADES]

        # ---------- Classes ----------
        class_ids: list[int] = []
        for gid in grade_ids:
            for idx in range(1, CLASSES_PER_G+1):
                cname = f"{fake.random_uppercase_letter()}-{idx}"
                cid = post(f"{s.base_url}/classes",
                           {"name": cname, "gradeId": gid}, s, logf)
                class_ids.append(cid)

        # ---------- Subjects ----------
        subj_ids = [post(f"{s.base_url}/subjects", {"name": n}, s, logf)
                    for n in SUBJECTS]

        # ---------- Teachers ----------
        teacher_ids = [post(f"{s.base_url}/teachers", {
                                "fullName": fake.name(),
                                "email":    fake.unique.ascii_safe_email(),
                           }, s, logf)
                       for _ in range(TEACHERS)]

        # (❗) teacher-subject relation is NOT exposed yet → skip

        # ---------- Students ----------
        student_ids: list[int] = []
        for cid in class_ids:
            for _ in range(STUDENTS_PER_C):
                sid = post(f"{s.base_url}/students", {
                               "fullName": fake.name(),
                               "email":    fake.unique.ascii_safe_email(),
                               "classId":  cid
                           }, s, logf)
                student_ids.append(sid)

        # ---------- Lessons / Exams / Assignments ----------
        lesson_ids: list[int] = []
        today = date.today()
        for (cid, sid) in itertools.product(class_ids, subj_ids):
            for _ in range(LESSONS_PER_SC):
                tid = random.choice(teacher_ids)
                l_id = post(f"{s.base_url}/lessons", {
                                "topic":      fake.sentence(nb_words=4),
                                "lessonDate": str(today - timedelta(days=random.randint(0, 30))),
                                "subjectId":  sid,
                                "teacherId":  tid,
                                "classId":    cid
                            }, s, logf)
                lesson_ids.append(l_id)

                # exam
                exam_id = post(f"{s.base_url}/exams", {
                                   "title":    f"Quiz {fake.word()}",
                                   "examDate": str(today + timedelta(days=random.randint(1, 45))),
                                   "lessonId": l_id
                               }, s, logf)
                # assignment
                post(f"{s.base_url}/assignments", {
                         "title":   f"HW {random.randint(1,60)}",
                         "dueDate": str(today + timedelta(days=random.randint(2, 20))),
                         "lessonId": l_id
                     }, s, logf)

                # one result per pupil in class
                class_students = [stu for stu in student_ids
                                  if requests.get(f"{s.base_url}/students/{stu}").json()["classId"] == cid]
                for stu in class_students:
                    post(f"{s.base_url}/results", {
                             "score":     round(random.uniform(40,100), 1),
                             "studentId": stu,
                             "examId":    exam_id
                         }, s, logf)

        # ---------- Attendance ----------
        for l_id in tqdm(lesson_ids, desc="Attendance"):
            clid = requests.get(f"{s.base_url}/lessons/{l_id}").json()["classId"]
            pupils = [stu for stu in student_ids
                      if requests.get(f"{s.base_url}/students/{stu}").json()["classId"] == clid]
            for stu in pupils:
                post(f"{s.base_url}/attendances", {
                         "status":    rand_status(),
                         "studentId": stu,
                         "lessonId":  l_id
                     }, s, logf)

        # ---------- Events / Announcements ----------
        for cid in class_ids + [None]:  # last iteration == whole-school
            for _ in range(5):
                ev_payload = {
                    "title":       fake.sentence(nb_words=6),
                    "description": fake.paragraph(),
                    "startsAt":    datetime.now().isoformat(),
                    "endsAt":      (datetime.now()+timedelta(hours=2)).isoformat(),
                    "classId":     cid
                }
                post(f"{s.base_url}/events", ev_payload, s, logf)

                ann_payload = {
                    "title":   fake.sentence(nb_words=4),
                    "content": fake.paragraph(nb_sentences=4),
                    "publishedAt": datetime.now().isoformat(),
                    "classId": cid
                }
                post(f"{s.base_url}/announcements", ann_payload, s, logf)

    logf.close()
    print(f"\n✔  Done – log written to {log_file.relative_to(Path.cwd())}")

# ────────────────────────────── CLI ──────────────────────────────────────
if __name__ == "__main__":
    ap = argparse.ArgumentParser(description="Seed the School-Management API")
    ap.add_argument("--base-url", default="http://localhost:8081/api",
                    help="Base URL of the running REST API")
    ap.add_argument("--out-dir", default="seed_out",
                    help="Where to write the seed_log & any artefacts")
    ap.add_argument("--wipe-first", action="store_true",
                    help="Call an external helper to drop & recreate the DB "
                         "(needs to be configured by you)")
    cfg = ap.parse_args()

    if cfg.wipe_first:
        print("💣  --wipe-first specified – remember to reset the DB yourself")
        # you might shell-out to docker-exec here

    try:
        seed(cfg.base_url, Path(cfg.out_dir))
    except requests.HTTPError as err:
        sys.exit("❌  Aborted: " + err.response.text[:400])
