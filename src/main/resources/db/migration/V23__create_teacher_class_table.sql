/* teacher ⇄ class join */
CREATE TABLE teacher_class (
    teacher_id BIGINT NOT NULL,
    class_id   BIGINT NOT NULL,
    PRIMARY KEY (teacher_id, class_id),

    CONSTRAINT fk_tc_teacher FOREIGN KEY (teacher_id)
        REFERENCES teacher(id) ON DELETE CASCADE,
    CONSTRAINT fk_tc_class   FOREIGN KEY (class_id)
        REFERENCES school_class(id) ON DELETE CASCADE
);

-- keep look-ups fast
CREATE INDEX idx_tc_class   ON teacher_class(class_id);
CREATE INDEX idx_tc_teacher ON teacher_class(teacher_id);
