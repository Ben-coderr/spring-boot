CREATE TABLE subject_grade_scheme(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id  BIGINT NOT NULL,
    grade_id    BIGINT NOT NULL,
    coefficient INT    NOT NULL,
    UNIQUE(subject_id, grade_id),
    FOREIGN KEY (subject_id) REFERENCES subject(id),
    FOREIGN KEY (grade_id)   REFERENCES grade(id)
);


CREATE TABLE mark_component (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  scheme_id  BIGINT NOT NULL,
  kind       VARCHAR(30) NOT NULL,            -- 'CC', 'EXAM', 'ATTENDANCE'
  weight     DECIMAL(5,2) NOT NULL,          
  UNIQUE(scheme_id, kind),
  FOREIGN KEY (scheme_id) REFERENCES subject_grade_scheme(id)
);

ALTER TABLE result
  ADD COLUMN component_id BIGINT;

ALTER TABLE result
  ADD CONSTRAINT fk_result_component
      FOREIGN KEY (component_id) REFERENCES mark_component(id);
