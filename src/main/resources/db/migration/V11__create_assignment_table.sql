CREATE TABLE assignment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    due_date DATE,
    lesson_id BIGINT NOT NULL,
    CONSTRAINT fk_assignment_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id)
);
