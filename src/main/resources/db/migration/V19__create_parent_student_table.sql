-- Parent–Student link
CREATE TABLE parent_student (
    parent_id  BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    PRIMARY KEY (parent_id, student_id),

    CONSTRAINT fk_ps_parent
        FOREIGN KEY (parent_id)
        REFERENCES parent(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ps_student
        FOREIGN KEY (student_id)
        REFERENCES student(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_ps_student ON parent_student (student_id);
