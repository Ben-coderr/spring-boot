ALTER TABLE student
  ADD COLUMN surname      VARCHAR(120) AFTER full_name,
  ADD COLUMN username     VARCHAR(120) UNIQUE,
  ADD COLUMN img          VARCHAR(255),
  ADD COLUMN blood_type   VARCHAR(3);     -- “O+”, “A-”, …

-- Optional convenience index if you’ll search on username
CREATE INDEX idx_student_username ON student (username);
