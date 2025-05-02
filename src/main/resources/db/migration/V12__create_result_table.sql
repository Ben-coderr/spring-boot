CREATE TABLE result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    score DOUBLE NOT NULL,
    student_id BIGINT NOT NULL,
    exam_id BIGINT NOT NULL,
    CONSTRAINT fk_result_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_result_exam    FOREIGN KEY (exam_id)    REFERENCES exam(id)
);
