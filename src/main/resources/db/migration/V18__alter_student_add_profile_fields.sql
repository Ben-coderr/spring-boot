-- Extra columns for student profile
ALTER TABLE student
  ADD COLUMN date_of_birth DATE NOT NULL AFTER email,
  ADD COLUMN gender VARCHAR(10) NOT NULL AFTER date_of_birth,
  ADD COLUMN phone VARCHAR(25) NULL,
  ADD COLUMN address VARCHAR(255) NULL,
  ADD COLUMN enrollment_date DATE NULL,
  ADD COLUMN status VARCHAR(15) NOT NULL DEFAULT 'ACTIVE',
  ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
  
-- Index often-queried fields
CREATE INDEX idx_student_class ON student (class_id);
CREATE INDEX idx_student_status ON student (status);
