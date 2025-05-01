#!/usr/bin/env python3
"""
seed_school.py – populate the Spring-Boot School-Management API
"""

from __future__ import annotations

import argparse
import itertools
import json
import random
import sys
from datetime import date, datetime, timedelta
from pathlib import Path
from typing import Final

import requests
from faker import Faker
from tqdm import tqdm

fake = Faker()

# ───────────────────────── constants ──────────────────────────
GENDERS:   Final[list[str]] = ["MALE", "FEMALE"]
STATUSES:  Final[list[str]] = ["ACTIVE", "GRADUATED", "INACTIVE"]
GRADES:    Final[list[int]] = [1, 2, 3]

CLASSES_PER_G  = 3
TEACHERS       = 15
SUBJECTS       = ["Maths", "Science", "English", "History", "Geography"]
STUDENTS_PER_C = 25
LESSONS_PER_SC = 2

ATTENDANCE_RATE = 0.92
WEEK_DAYS       = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"]
BLOOD_TYPES     = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"]
TIMEOUT         = 30
# ──────────────────────────────────────────────────────────────


# ---------- helpers -----------------------------------------------------------
def add_years(d: date, *, years: int) -> date:
    """`d` + N years but keep 28 Feb if the target year has no 29 Feb."""
    try:
        return d.replace(year=d.year + years)
    except ValueError:          # 29 Feb into a non-leap year
        return d.replace(year=d.year + years, day=28)


def post(url: str, payload: dict, session: requests.Session, logf) -> int:
    """POSTs, prints and logs the response; returns the new `id`."""
    r = session.post(url, json=payload, timeout=TIMEOUT)
    r.raise_for_status()
    res = r.json()
    print("⇧", url.replace(session.base_url, ""), "→", res["id"], flush=True)
    logf.write(
        json.dumps(
            {"url": url, "payload": payload, "response": res},
            ensure_ascii=False,
        )
        + "\n"
    )
    return res["id"]


def rand_status() -> str:
    """PRESENT ≈ 92 %, ABSENT ≈ 6 %, LATE ≈ 2 %."""
    return random.choices(
        ["PRESENT", "ABSENT", "LATE"],
        weights=[ATTENDANCE_RATE, 1 - ATTENDANCE_RATE - 0.02, 0.02],
    )[0]


# ─────────────────────────── seeder ───────────────────────────
def seed(base_url: str, out_dir: Path) -> None:
    out_dir.mkdir(parents=True, exist_ok=True)
    logf = (out_dir / "seed_log.ndjson").open("w", encoding="utf-8")

    with requests.Session() as s:
        s.base_url = base_url.rstrip("/")

        # ---------- Admin ----------
        post(
            f"{s.base_url}/admins",
            {
                "fullName": "Super Admin",
                "email": f"root-{int(datetime.utcnow().timestamp())}@school.test",
            },
            s,
            logf,
        )

        # ---------- Grades ----------
        grade_ids = []
        for lvl in GRADES:
            grade_ids.append(
                post(
                    f"{s.base_url}/grades",
                    {"name": f"Grade {lvl}", "level": lvl},
                    s,
                    logf,
                )
            )

        # ---------- Teachers ----------
        teacher_ids = [
            post(
                f"{s.base_url}/teachers",
                {"fullName": fake.name(), "email": fake.unique.ascii_safe_email()},
                s,
                logf,
            )
            for _ in range(TEACHERS)
        ]

        # ---------- Subjects ----------
        subj_ids = [
            post(f"{s.base_url}/subjects", {"name": n}, s, logf) for n in SUBJECTS
        ]

        # ---------- Classes ----------
        class_ids: list[int] = []
        for gid in grade_ids:
            for idx in range(1, CLASSES_PER_G + 1):
                cname = f"{fake.random_uppercase_letter()}-{idx}"
                capacity = random.randint(25, 35)
                supervisor = (
                    random.choice(teacher_ids) if random.random() < 0.7 else None
                )

                payload = {
                    "name": cname,
                    "gradeId": gid,
                    "capacity": capacity,
                }
                if supervisor:
                    payload["supervisorId"] = supervisor

                class_ids.append(post(f"{s.base_url}/classes", payload, s, logf))

        # Map class → grade once (avoid an extra GET per student later)
        class_to_grade = {
            cid: requests.get(f"{s.base_url}/classes/{cid}", timeout=TIMEOUT).json()[
                "gradeId"
            ]
            for cid in class_ids
        }

        # ---------- Students ----------
        student_ids: list[int] = []
        for cid in class_ids:
            for _ in range(STUDENTS_PER_C):
                dob = fake.date_of_birth(minimum_age=6, maximum_age=18)
                payload = {
                    "fullName": fake.name(),
                    "surname": fake.last_name(),
                    "username": fake.user_name(),
                    "email": fake.unique.ascii_safe_email(),
                    "img": fake.image_url(width=200, height=200),
                    "bloodType": random.choice(BLOOD_TYPES),
                    "classId": cid,
                    "gradeId": class_to_grade[cid],
                    "dateOfBirth": dob.isoformat(),
                    "gender": random.choice(GENDERS),
                    "phone": fake.phone_number(),
                    "address": fake.address().replace("\n", ", "),
                    "enrollmentDate": add_years(dob, years=6).isoformat(),
                    "status": random.choice(STATUSES),
                }
                student_ids.append(
                    post(f"{s.base_url}/students", payload, s, logf)
                )

        # ---------- Parents (2 per child) ----------
        for stu in student_ids:
            for fn in (fake.name_female(), fake.name_male()):
                post(
                    f"{s.base_url}/parents",
                    {
                        "fullName": fn,
                        "email": fake.unique.ascii_safe_email(),
                        "childIds": [stu],
                    },
                    s,
                    logf,
                )

        # ---------- Lessons / Exams / Assignments ----------
        lesson_ids: list[int] = []
        today = date.today()

        for cid, sid in itertools.product(class_ids, subj_ids):
            for _ in range(LESSONS_PER_SC):
                tid = random.choice(teacher_ids)

                # timetable slot
                day = random.choice(WEEK_DAYS)
                start_hour = random.randint(8, 14)
                start_time = f"{start_hour:02d}:00"
                end_time = f"{start_hour + random.choice([1, 1, 2]):02d}:00"

                l_id = post(
                    f"{s.base_url}/lessons",
                    {
                        "topic": fake.sentence(nb_words=4),
                        "day": day,
                        "startTime": start_time,
                        "endTime": end_time,
                        "subjectId": sid,
                        "teacherId": tid,
                        "classId": cid,
                    },
                    s,
                    logf,
                )
                lesson_ids.append(l_id)

                # exam
                exam_id = post(
                    f"{s.base_url}/exams",
                    {
                        "title": f"Quiz {fake.word()}",
                        "examDate": str(today + timedelta(days=random.randint(1, 45))),
                        "lessonId": l_id,
                    },
                    s,
                    logf,
                )

                # assignment
                post(
                    f"{s.base_url}/assignments",
                    {
                        "title": f"HW {random.randint(1,60)}",
                        "dueDate": str(today + timedelta(days=random.randint(2, 20))),
                        "lessonId": l_id,
                    },
                    s,
                    logf,
                )

                # results (one per pupil in the class)
                class_students = [
                    stu
                    for stu in student_ids
                    if requests.get(
                        f"{s.base_url}/students/{stu}", timeout=TIMEOUT
                    ).json()["classId"]
                    == cid
                ]
                for stu in class_students:
                    post(
                        f"{s.base_url}/results",
                        {
                            "score": round(random.uniform(40, 100), 1),
                            "studentId": stu,
                            "examId": exam_id,
                        },
                        s,
                        logf,
                    )

        # ---------- Attendance ----------
        for l_id in tqdm(lesson_ids, desc="Attendance"):
            clid = requests.get(
                f"{s.base_url}/lessons/{l_id}", timeout=TIMEOUT
            ).json()["classId"]
            pupils = [
                stu
                for stu in student_ids
                if requests.get(
                    f"{s.base_url}/students/{stu}", timeout=TIMEOUT
                ).json()["classId"]
                == clid
            ]
            for stu in pupils:
                post(
                    f"{s.base_url}/attendances",
                    {"status": rand_status(), "studentId": stu, "lessonId": l_id},
                    s,
                    logf,
                )

        # ---------- Events & Announcements ----------
        for cid in class_ids + [None]:  # None ⇒ whole-school
            for _ in range(5):
                now = datetime.now()
                post(
                    f"{s.base_url}/events",
                    {
                        "title": fake.sentence(nb_words=6),
                        "description": fake.paragraph(),
                        "startsAt": now.isoformat(),
                        "endsAt": (now + timedelta(hours=2)).isoformat(),
                        "classId": cid,
                    },
                    s,
                    logf,
                )
                post(
                    f"{s.base_url}/announcements",
                    {
                        "title": fake.sentence(nb_words=4),
                        "content": fake.paragraph(nb_sentences=4),
                        "publishedAt": now.isoformat(),
                        "classId": cid,
                    },
                    s,
                    logf,
                )

    logf.close()
    print(f"\n✔  Seeding complete – log written to {logf.name}")


# ───────────────────────────── CLI ─────────────────────────────
if __name__ == "__main__":
    p = argparse.ArgumentParser(description="Seed the School-Management API")
    p.add_argument("--base-url", default="http://localhost:8081/api")
    p.add_argument("--out-dir", default="seed_out")
    p.add_argument("--wipe-first", action="store_true")
    cfg = p.parse_args()

    if cfg.wipe_first:
        print("💣  --wipe-first given – make sure the DB is clean first")

    try:
        seed(cfg.base_url, Path(cfg.out_dir))
    except requests.HTTPError as e:
        sys.exit("❌  Aborted: " + e.response.text[:400])
