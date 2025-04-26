/* MySQL 8 syntax — adjust types only if you are on Postgres */

ALTER TABLE school_class
    ADD COLUMN capacity INT NOT NULL DEFAULT 30,
    ADD COLUMN supervisor_id BIGINT NULL,
    ADD CONSTRAINT fk_class_supervisor
        FOREIGN KEY (supervisor_id) REFERENCES teacher(id);

CREATE INDEX idx_class_supervisor ON school_class(supervisor_id);
