/* ----------------------------------------------------------
   V4 – move staff / parents / students to the central users table
----------------------------------------------------------- */

-- 1. add FK column (nullable for now so the script succeeds even
--    before we back-fill values)
ALTER TABLE parent    ADD COLUMN user_id BIGINT;
ALTER TABLE teacher   ADD COLUMN user_id BIGINT;
ALTER TABLE student   ADD COLUMN user_id BIGINT;

-- 2. get rid of the legacy password columns
ALTER TABLE parent    DROP COLUMN password;
ALTER TABLE teacher   DROP COLUMN password;
ALTER TABLE student   DROP COLUMN password;

-- 3. wire the three tables to USERS
ALTER TABLE parent
  ADD CONSTRAINT fk_parent_user
      FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE teacher
  ADD CONSTRAINT fk_teacher_user
      FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE student
  ADD CONSTRAINT fk_student_user
      FOREIGN KEY (user_id) REFERENCES users(id);
