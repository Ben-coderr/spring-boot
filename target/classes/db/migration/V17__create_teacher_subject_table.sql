CREATE TABLE teacher_subject (
    teacher_id  BIGINT NOT NULL,
    subject_id  BIGINT NOT NULL,
    PRIMARY KEY (teacher_id, subject_id),

    CONSTRAINT fk_ts_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES teacher(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ts_subject
        FOREIGN KEY (subject_id)
        REFERENCES subject(id)
        ON DELETE CASCADE
);
