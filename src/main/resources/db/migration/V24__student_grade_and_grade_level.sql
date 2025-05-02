/* 1 ── add grade_id to student (nullable for legacy rows) */
ALTER TABLE student ADD COLUMN grade_id BIGINT NULL;
ALTER TABLE student
    ADD CONSTRAINT fk_student_grade
        FOREIGN KEY (grade_id) REFERENCES grade(id);

/* 2 ── change grade.name → integer level */
ALTER TABLE grade
    ADD COLUMN level INT UNIQUE;
UPDATE grade SET level = CAST(name AS UNSIGNED);   -- best-effort if you used “1”,“2”,…
ALTER TABLE grade DROP COLUMN name;
