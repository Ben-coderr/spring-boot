CREATE TABLE exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    exam_date DATE,
    lesson_id BIGINT NOT NULL,
    CONSTRAINT fk_exam_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id)
);
