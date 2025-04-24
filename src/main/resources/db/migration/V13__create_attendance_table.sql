CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(10) NOT NULL,
    student_id BIGINT NOT NULL,
    lesson_id  BIGINT NOT NULL,
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_attendance_lesson  FOREIGN KEY (lesson_id)  REFERENCES lesson(id)
);
