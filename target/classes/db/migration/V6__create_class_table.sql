CREATE TABLE class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    grade_id BIGINT NOT NULL,
    CONSTRAINT fk_class_grade FOREIGN KEY (grade_id) REFERENCES grade(id),
    UNIQUE KEY uk_class_name_grade (name, grade_id)
);
