/* V26 – auth columns (password already existed on admin) */

-- ADMIN  ───────────────
ALTER TABLE admin
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'ADMIN';

-- TEACHER ──────────────
ALTER TABLE teacher
    ADD COLUMN password VARCHAR(255),
    ADD COLUMN role     VARCHAR(20) NOT NULL DEFAULT 'TEACHER';

-- STUDENT ──────────────
ALTER TABLE student
    ADD COLUMN password VARCHAR(255),
    ADD COLUMN role     VARCHAR(20) NOT NULL DEFAULT 'STUDENT';

-- PARENT  ──────────────
ALTER TABLE parent
    ADD COLUMN password VARCHAR(255),
    ADD COLUMN role     VARCHAR(20) NOT NULL DEFAULT 'PARENT';
