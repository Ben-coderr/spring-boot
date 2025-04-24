ALTER TABLE student ADD COLUMN class_id BIGINT NOT NULL;
ALTER TABLE student
  ADD CONSTRAINT fk_student_class FOREIGN KEY (class_id)
  REFERENCES school_class(id);
