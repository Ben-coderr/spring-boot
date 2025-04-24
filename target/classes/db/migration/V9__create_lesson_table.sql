CREATE TABLE lesson (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    topic VARCHAR(150) NOT NULL,
    lesson_date DATE,
    subject_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    class_id  BIGINT NOT NULL,
    CONSTRAINT fk_lesson_subject FOREIGN KEY (subject_id) REFERENCES subject(id),
    CONSTRAINT fk_lesson_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    CONSTRAINT fk_lesson_class   FOREIGN KEY (class_id)   REFERENCES school_class(id)
);
