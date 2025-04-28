ALTER TABLE student
  ADD COLUMN username VARCHAR(120) NOT NULL UNIQUE
            AFTER surname;     -- sits next to surname

/* Optional: If you added bogus default values earlier, drop them:
   ALTER TABLE student MODIFY COLUMN username VARCHAR(120) NOT NULL UNIQUE; */
