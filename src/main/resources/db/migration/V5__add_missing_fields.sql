ALTER TABLE student
    ADD COLUMN matricule VARCHAR(50),
    ADD COLUMN place_of_birth VARCHAR(255);

ALTER TABLE teacher
    ADD COLUMN place_of_birth VARCHAR(255);

ALTER TABLE subject
  ADD COLUMN cc_weight         INT(1),
  ADD COLUMN exam_weight       INT(1),
  ADD COLUMN attendance_weight INT(1);


ALTER TABLE result
  ADD COLUMN kind VARCHAR(30) NOT NULL DEFAULT 'EXAM';