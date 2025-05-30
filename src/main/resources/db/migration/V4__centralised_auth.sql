ALTER TABLE parent    ADD COLUMN user_id BIGINT;
ALTER TABLE teacher   ADD COLUMN user_id BIGINT;
ALTER TABLE student   ADD COLUMN user_id BIGINT;


ALTER TABLE parent    DROP COLUMN password;
ALTER TABLE teacher   DROP COLUMN password;
ALTER TABLE student   DROP COLUMN password;

-- wire the three tables to USERS
ALTER TABLE parent
  ADD CONSTRAINT fk_parent_user
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE teacher
  ADD CONSTRAINT fk_teacher_user
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE student
  ADD CONSTRAINT fk_student_user
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
