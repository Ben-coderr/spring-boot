#!/usr/bin/env python3
"""
Seed the Edusphere API with fake data.

Usage:
    python fill_via_api.py --host http://localhost:8083 --user admin --password password

Dependencies:
    pip install requests faker tqdm
"""

from __future__ import annotations

import argparse
import itertools
import random
import string
import sys
from collections import defaultdict
from typing import Dict, List

import requests
from faker import Faker
from tqdm import tqdm

# --------------------------------------------------------------------------- helpers

fake = Faker()
rand = random.Random()


def _rand_phone() -> str:
    """12-digit random “phone” number."""
    return fake.msisdn()[:12]


def _rand_blood() -> str:
    return rand.choice(["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"])


def _unique(username: str, seen: set[str]) -> str:
    """
    Make username unique for *this* run by appending a -xxxx suffix if needed.
    """
    if username not in seen:
        seen.add(username)
        return username

    base = username.split("@")[0]
    while True:
        candidate = f"{base}-{''.join(rand.choices(string.ascii_lowercase + string.digits, k=6))}"
        if candidate not in seen:
            seen.add(candidate)
            return candidate


def _post(session: requests.Session, url: str, payload: dict, retries: int = 1):
    """
    Thin wrapper around POST that raises RuntimeError on non-success.
    If we get a duplicate-username type error, we retry once with a new suffix.
    """
    while True:
        r = session.post(url, json=payload)
        if r.status_code in (200, 201):
            return r.json()

        if (
            retries
            and r.status_code in (400, 409, 500)
            and "Duplicate" in r.text
        ):
            retries -= 1
            payload = payload.copy()  # shallow copy is enough
            usr = payload.get("user") or payload  # depends on entity
            usr["username"] = _unique(usr["username"], set())  # ensure new
            continue

        raise RuntimeError(f"POST {url} → {r.status_code} : {r.text}")


# --------------------------------------------------------------------------- entity builders

def create_grades(s: requests.Session, host: str) -> Dict[int, int]:
    return {lvl: _post(s, f"{host}/grades", {"level": lvl})["id"] for lvl in range(1, 13)}


def create_subjects(s: requests.Session, host: str) -> Dict[str, int]:
    names = [
        "Math", "Physics", "Chemistry", "Biology", "History",
        "Geography", "English", "French", "Computer Science", "Arts"
    ]
    return {n: _post(s, f"{host}/subjects", {"name": n})["id"] for n in names}


def create_parents(s, host, n: int, seen: set[str]) -> List[int]:
    ids = []
    for _ in tqdm(range(n), desc="parents"):
        email = fake.email()
        payload = {
            "fullName": fake.name(),
            "phone": _rand_phone(),
            "email": email,
            "address": fake.address(),
            "user": {
                "username": _unique(email, seen),
                "password": "pwd"
            }
        }
        ids.append(_post(s, f"{host}/parents", payload)["id"])
    return ids


def create_teachers(s, host, subjects, n: int, seen: set[str]) -> List[int]:
    ids, subs = [], list(subjects.values())
    for _ in tqdm(range(n), desc="teachers"):
        email = fake.email()
        payload = {
            "fullName": fake.name(),
            "email": email,
            "phone": _rand_phone(),
            "subject": {"id": rand.choice(subs)},
            "img": fake.image_url(width=128, height=128),
            "bloodType": _rand_blood(),
            "sex": rand.choice(["MALE", "FEMALE"]),
            "birthday": str(fake.date_of_birth(minimum_age=25, maximum_age=55)),
            "user": {
                "username": _unique(email, seen),
                "password": "pwd"
            }
        }
        ids.append(_post(s, f"{host}/teachers", payload)["id"])
    return ids


def create_classes(s, host, grades: Dict[int, int], teachers: List[int], per_grade: int) -> List[int]:
    ids = []
    for level, gid in grades.items():
        for idx in range(per_grade):
            name = f"{level}{chr(65 + idx)}"
            payload = {
                "name": f"Class {name}",
                "capacity": 30,
                "grade": {"id": gid},
                "supervisor": {"id": rand.choice(teachers)}
            }
            ids.append(_post(s, f"{host}/classes", payload)["id"])
    return ids


def create_students(s, host, parents, classes, n: int, seen: set[str]) -> List[int]:
    ids = []
    for _ in tqdm(range(n), desc="students"):
        cid = rand.choice(classes)
        email = fake.email()
        payload = {
            "fullName": fake.name(),
            "email": email,
            "phone": _rand_phone(),
            "address": fake.address(),
            "img": fake.image_url(width=128, height=128),
            "bloodType": _rand_blood(),
            "sex": rand.choice(["MALE", "FEMALE"]),
            "birthday": str(fake.date_of_birth(minimum_age=6, maximum_age=18)),
            "parent": {"id": rand.choice(parents)},
            "schoolClass": {"id": cid},
            "user": {
                "username": _unique(email, seen),
                "password": "pwd"
            }
        }
        ids.append(_post(s, f"{host}/students", payload)["id"])
    return ids


def create_lessons(s, host, subjects, teachers, classes, days) -> List[int]:
    ids = []
    for cls, day in tqdm(itertools.product(classes, days), desc="lessons", total=len(classes) * len(days)):
        payload = {
            "topic": f"{fake.word().title()} - {day}",
            "lessonDate": str(fake.date_between('-30d', 'today')),
            "day": day.upper(),
            "startTime": "08:00:00",
            "endTime": "09:00:00",
            "subject": {"id": rand.choice(list(subjects.values()))},
            "teacher": {"id": rand.choice(teachers)},
            "schoolClass": {"id": cls}
        }
        ids.append(_post(s, f"{host}/lessons", payload)["id"])
    return ids


def create_exams_and_assignments(s, host, lessons):
    exam_ids, ass_ids = [], []
    for lid in tqdm(lessons, desc="exams+ass"):
        exam_ids.append(_post(s, f"{host}/exams", {
            "title": f"Test {fake.word().title()}",
            "examDate": str(fake.date_between('-15d', '+15d')),
            "lesson": {"id": lid}
        })["id"])
        ass_ids.append(_post(s, f"{host}/assignments", {
            "title": f"HW {fake.word().title()}",
            "dueDate": str(fake.date_between('-10d', '+10d')),
            "lesson": {"id": lid}
        })["id"])
    return exam_ids, ass_ids


def create_results(s, host, students, exams):
    for sid in tqdm(students, desc="results"):
        for eid in rand.sample(exams, min(5, len(exams))):
            _post(s, f"{host}/results", {
                "score": round(rand.uniform(30, 100), 1),
                "student": {"id": sid},
                "exam": {"id": eid},
                "isFinal": rand.choice([True, False])
            })


def create_attendance(s, host, students, lessons):
    statuses = ["PRESENT", "ABSENT", "LATE"]
    for sid in tqdm(students, desc="attendance"):
        for lid in rand.sample(lessons, min(15, len(lessons))):
            _post(s, f"{host}/attendances", {
                "status": rand.choice(statuses),
                "date": str(fake.date_between('-30d', 'today')),
                "student": {"id": sid},
                "lesson": {"id": lid}
            })


def create_announcements(s, host, classes, n: int):
    for _ in tqdm(range(n), desc="announcements"):
        _post(s, f"{host}/announcements", {
            "title": fake.sentence(nb_words=6),
            "content": fake.paragraph(nb_sentences=4),
            "schoolClass": {"id": rand.choice(classes)}
        })


# --------------------------------------------------------------------------- main

def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument('--host', default='http://localhost:8083')
    ap.add_argument('--user', default='admin')
    ap.add_argument('--password', default='password')
    ap.add_argument('--parents', type=int, default=50)
    ap.add_argument('--teachers', type=int, default=20)
    ap.add_argument('--students', type=int, default=120)
    args = ap.parse_args()

    # ----------------------------------------------------------------- AUTH
    sess = requests.Session()
    try:
        resp = sess.post(f"{args.host}/auth/login",
                         json={"username": args.user, "password": args.password})
        resp.raise_for_status()
        token = resp.json().get("token") or resp.json().get("accessToken")
        if not token:
            raise RuntimeError("token missing in auth response")
    except Exception as ex:
        sys.exit(f"❌  Authentication failed: {ex}\n{resp.text if 'resp' in locals() else ''}")

    sess.headers["Authorization"] = f"Bearer {token}"

    # ----------------------------------------------------------------- seed
    print('Seeding reference data…')
    used_usernames: set[str] = set()

    grades = create_grades(sess, args.host)
    subjects = create_subjects(sess, args.host)

    parents = create_parents(sess, args.host, args.parents, used_usernames)
    teachers = create_teachers(sess, args.host, subjects, args.teachers, used_usernames)
    classes = create_classes(sess, args.host, grades, teachers, per_grade=2)
    students = create_students(sess, args.host, parents, classes, args.students, used_usernames)

    days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]
    lessons = create_lessons(sess, args.host, subjects, teachers, classes, days)
    exams, assignments = create_exams_and_assignments(sess, args.host, lessons)

    create_results(sess, args.host, students, exams)
    create_attendance(sess, args.host, students, lessons)
    create_announcements(sess, args.host, classes, n=15)

    print('✅  Done – database is now populated.')


if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        sys.exit("\nInterrupted by user.")
