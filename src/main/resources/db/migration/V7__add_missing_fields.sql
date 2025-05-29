ALTER TABLE student
    ADD COLUMN matricule VARCHAR(50),
    ADD COLUMN place_of_birth VARCHAR(255);

ALTER TABLE teacher
    ADD COLUMN place_of_birth VARCHAR(255);
