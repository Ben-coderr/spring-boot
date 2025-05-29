/* ------------------------------------------------------------------
   ENUM-like helpers (optional—can also stay VARCHAR if your MySQL
   flavour dislikes ENUM). Comment-out if you prefer plain text.
------------------------------------------------------------------- */
-- CREATE TYPE user_sex   AS ENUM('MALE','FEMALE');   -- PostgreSQL style
-- MySQL: simply use ENUM below.

/* ------------------------------------------------------------------
   1.  Core reference tables
------------------------------------------------------------------- */
CREATE TABLE grade (
  id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  level  INT NOT NULL UNIQUE
);

CREATE TABLE subject (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL UNIQUE
);

/* ------------------------------------------------------------------
   2.  People
------------------------------------------------------------------- */
CREATE TABLE parent (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name  VARCHAR(120) NOT NULL,
  phone      VARCHAR(25)  UNIQUE,
  email      VARCHAR(120) UNIQUE,
  address    VARCHAR(255),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE teacher (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name   VARCHAR(120) NOT NULL,
  email       VARCHAR(120) UNIQUE,
  phone       VARCHAR(25)  UNIQUE,
  password    VARCHAR(255) NOT NULL,
  subject_id  BIGINT,                         -- one main subject per teacher
  img         VARCHAR(255),
  blood_type  VARCHAR(3),
  sex         ENUM('MALE','FEMALE'),
  birthday    DATE,
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (subject_id) REFERENCES subject(id)
);

CREATE TABLE admin (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name  VARCHAR(120) NOT NULL,
  email      VARCHAR(120) UNIQUE,
  password   VARCHAR(255) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

/* ------------------------------------------------------------------
   3.  Classes & pupils
------------------------------------------------------------------- */
CREATE TABLE school_class (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  name           VARCHAR(100) NOT NULL UNIQUE,
  capacity       INT DEFAULT 30,
  grade_id       BIGINT NOT NULL,
  supervisor_id  BIGINT,                         -- teacher who oversees the class
  FOREIGN KEY (grade_id)      REFERENCES grade(id),
  FOREIGN KEY (supervisor_id) REFERENCES teacher(id)
);

CREATE TABLE student (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name       VARCHAR(120) NOT NULL,
  surname         VARCHAR(120),
  email           VARCHAR(120) UNIQUE,
  phone           VARCHAR(25)  UNIQUE,
  password        VARCHAR(255) NOT NULL,
  address         VARCHAR(255),
  img             VARCHAR(255),
  blood_type      VARCHAR(3),
  sex             ENUM('MALE','FEMALE'),
  birthday        DATE,
  created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,

  parent_id       BIGINT,
  school_class_id BIGINT NOT NULL,

  FOREIGN KEY (parent_id)       REFERENCES parent(id),
  FOREIGN KEY (school_class_id) REFERENCES school_class(id)
);

/* ------------------------------------------------------------------
   4.  Timetable & assessment
------------------------------------------------------------------- */
CREATE TABLE lesson (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  topic           VARCHAR(150) NOT NULL,
  lesson_date     DATE,
  day             VARCHAR(10),       -- e.g. 'MONDAY'
  start_time      TIME,
  end_time        TIME,

  subject_id      BIGINT,
  teacher_id      BIGINT,
  school_class_id BIGINT,

  FOREIGN KEY (subject_id)      REFERENCES subject(id),
  FOREIGN KEY (teacher_id)      REFERENCES teacher(id),
  FOREIGN KEY (school_class_id) REFERENCES school_class(id)
);

CREATE TABLE exam (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  title      VARCHAR(150),
  exam_date  DATE,
  lesson_id  BIGINT,
  FOREIGN KEY (lesson_id) REFERENCES lesson(id)
);

CREATE TABLE assignment (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  title      VARCHAR(150),
  due_date   DATE,
  lesson_id  BIGINT,
  FOREIGN KEY (lesson_id) REFERENCES lesson(id)
);

CREATE TABLE result (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  score      DOUBLE,
  student_id BIGINT,
  exam_id    BIGINT,
  FOREIGN KEY (student_id) REFERENCES student(id),
  FOREIGN KEY (exam_id)    REFERENCES exam(id)
);

CREATE TABLE attendance (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  status     VARCHAR(10),     -- PRESENT / ABSENT / LATE
  date       DATE,
  student_id BIGINT,
  lesson_id  BIGINT,
  FOREIGN KEY (student_id) REFERENCES student(id),
  FOREIGN KEY (lesson_id)  REFERENCES lesson(id)
);

/* ------------------------------------------------------------------
   5.  Misc announcements (no events table by request)
------------------------------------------------------------------- */
CREATE TABLE announcement (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  title           VARCHAR(150),
  content         TEXT,
  published_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
  school_class_id BIGINT,
  FOREIGN KEY (school_class_id) REFERENCES school_class(id)
);
