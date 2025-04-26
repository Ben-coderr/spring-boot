#!/usr/bin/env python3
"""
seed_school.py – populate the Spring-Boot School-Management API
"""

from __future__ import annotations
import argparse, itertools, json, random, sys
from datetime import date, datetime, timedelta
from pathlib import Path

import requests
from faker import Faker
from tqdm import tqdm

fake = Faker()

# ────────────────────────── constants ──────────────────────────
GENDERS = ["MALE", "FEMALE"]
GRADES  = ["Grade 1", "Grade 2", "Grade 3"]
CLASSES_PER_G, TEACHERS = 3, 15
SUBJECTS = ["Maths", "Science", "English", "History", "Geography"]
STUDENTS_PER_C, LESSONS_PER_SC = 25, 2
ATTENDANCE_RATE = 0.92
WEEK_DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"]
# ───────────────────────────────────────────────────────────────


def post(url: str, payload: dict, session: requests.Session, logf):
    r = session.post(url, json=payload)
    r.raise_for_status()
    res = r.json()
    print("⇧", url.replace(session.base_url, ""), "→", res["id"], flush=True)
    logf.write(json.dumps({"url": url, "payload": payload, "response": res}) + "\n")
    return res["id"]


def rand_status() -> str:
    return random.choices(
        ["PRESENT", "ABSENT", "LATE"],
        weights=[ATTENDANCE_RATE, 1 - ATTENDANCE_RATE - 0.02, 0.02]
    )[0]


# ─────────────────────────── main ──────────────────────────────
def seed(base_url: str, out_dir: Path):
    out_dir.mkdir(parents=True, exist_ok=True)
    logf = (out_dir / "seed_log.ndjson").open("w", encoding="utf-8")

    with requests.Session() as s:
        s.base_url = base_url.rstrip("/")

        # ---------- Admin ----------
        post(f"{s.base_url}/admins",
             {"fullName": "Super Admin",
              "email": f"root-{datetime.utcnow().timestamp():.0f}@school.test"},
             s, logf)

        # ---------- Grades ----------
        grade_ids = [post(f"{s.base_url}/grades", {"name": g}, s, logf) for g in GRADES]

        # ---------- Teachers ----------
        teacher_ids = [
            post(f"{s.base_url}/teachers",
                 {"fullName": fake.name(), "email": fake.unique.ascii_safe_email()},
                 s, logf)
            for _ in range(TEACHERS)
        ]

        # ---------- Subjects ----------
        subj_ids = [post(f"{s.base_url}/subjects", {"name": n}, s, logf)
                    for n in SUBJECTS]

        # ---------- Classes ----------
        class_ids: list[int] = []
        for gid in grade_ids:
            for idx in range(1, CLASSES_PER_G + 1):
                cname = f"{fake.random_uppercase_letter()}-{idx}"
                capacity = random.randint(25, 35)
                supervisor_id = random.choice(teacher_ids) if random.random() < 0.7 else None

                payload = {"name": cname, "gradeId": gid, "capacity": capacity}
                if supervisor_id:
                    payload["supervisorId"] = supervisor_id

                class_ids.append(post(f"{s.base_url}/classes", payload, s, logf))

        # ---------- Students ----------
        student_ids: list[int] = []
        for cid in class_ids:
            for _ in range(STUDENTS_PER_C):
                dob = fake.date_of_birth(minimum_age=6, maximum_age=18)
                student_ids.append(
                    post(f"{s.base_url}/students",
                         {"fullName": fake.name(),
                          "email": fake.unique.ascii_safe_email(),
                          "classId": cid,
                          "dateOfBirth": dob.isoformat(),
                          "gender": random.choice(GENDERS),
                          "enrollmentDate": dob.replace(year=dob.year + 6).isoformat()},
                         s, logf)
                )

        # ---------- Parents ----------
        for stu in student_ids:
            for parent_name in (fake.name_female(), fake.name_male()):
                post(f"{s.base_url}/parents",
                     {"fullName": parent_name,
                      "email": fake.unique.ascii_safe_email(),
                      "childIds": [stu]},
                     s, logf)

        # ---------- Lessons / Exams / Assignments ----------
        lesson_ids: list[int] = []
        today = date.today()

        for cid, sid in itertools.product(class_ids, subj_ids):
            for _ in range(LESSONS_PER_SC):
                tid = random.choice(teacher_ids)

                # timetable fields
                day = random.choice(WEEK_DAYS)
                start_hour = random.randint(8, 14)           # 08-14h
                start   = f"{start_hour:02d}:00"
                end     = f"{start_hour + random.choice([1, 1, 2]):02d}:00"

                l_id = post(f"{s.base_url}/lessons",
                            {"topic":     fake.sentence(nb_words=4),
                             "day":       day,
                             "startTime": start,
                             "endTime":   end,
                             "subjectId": sid,
                             "teacherId": tid,
                             "classId":   cid},
                            s, logf)
                lesson_ids.append(l_id)

                # ---------- exam ----------
                exam_id = post(f"{s.base_url}/exams",
                               {"title": f"Quiz {fake.word()}",
                                "examDate": str(today + timedelta(days=random.randint(1, 45))),
                                "lessonId": l_id},
                               s, logf)

                # ---------- assignment ----------
                post(f"{s.base_url}/assignments",
                     {"title": f"HW {random.randint(1,60)}",
                      "dueDate": str(today + timedelta(days=random.randint(2, 20))),
                      "lessonId": l_id},
                     s, logf)

                # results
                class_students = [stu for stu in student_ids
                                  if requests.get(f"{s.base_url}/students/{stu}").json()["classId"] == cid]
                for stu in class_students:
                    post(f"{s.base_url}/results",
                         {"score": round(random.uniform(40, 100), 1),
                          "studentId": stu,
                          "examId": exam_id},
                         s, logf)

        # ---------- Attendance ----------
        for l_id in tqdm(lesson_ids, desc="Attendance"):
            clid = requests.get(f"{s.base_url}/lessons/{l_id}").json()["classId"]
            pupils = [stu for stu in student_ids
                      if requests.get(f"{s.base_url}/students/{stu}").json()["classId"] == clid]
            for stu in pupils:
                post(f"{s.base_url}/attendances",
                     {"status": rand_status(), "studentId": stu, "lessonId": l_id},
                     s, logf)

        # ---------- Events & Announcements ----------
        for cid in class_ids + [None]:
            for _ in range(5):
                post(f"{s.base_url}/events",
                     {"title": fake.sentence(nb_words=6),
                      "description": fake.paragraph(),
                      "startsAt": datetime.now().isoformat(),
                      "endsAt": (datetime.now() + timedelta(hours=2)).isoformat(),
                      "classId": cid},
                     s, logf)
                post(f"{s.base_url}/announcements",
                     {"title": fake.sentence(nb_words=4),
                      "content": fake.paragraph(nb_sentences=4),
                      "publishedAt": datetime.now().isoformat(),
                      "classId": cid},
                     s, logf)

    logf.close()
    print(f"\n✔  Seeding complete – log written to {logf.name}")


# ─────────────────────────────── CLI ───────────────────────────────
if __name__ == "__main__":
    p = argparse.ArgumentParser()
    p.add_argument("--base-url", default="http://localhost:8081/api")
    p.add_argument("--out-dir", default="seed_out")
    p.add_argument("--wipe-first", action="store_true")
    cfg = p.parse_args()

    if cfg.wipe_first:
        print("💣  --wipe-first specified – reset the DB yourself")

    try:
        seed(cfg.base_url, Path(cfg.out_dir))
    except requests.HTTPError as e:
        sys.exit("❌  " + e.response.text[:400])
