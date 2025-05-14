/* 1-a) give every NULL row a placeholder hash
   (hash of the literal string “changeme” generated with PasswordHash util) */
UPDATE teacher SET password =
  '$2a$10$MtDv8oylmbWc9LzB/k3jYuz1kAkdNhkw1h1lOsuF9GIBKMsOF1iSm'
WHERE password IS NULL;

UPDATE parent  SET password =
  '$2a$10$MtDv8oylmbWc9LzB/k3jYuz1kAkdNhkw1h1lOsuF9GIBKMsOF1iSm'
WHERE password IS NULL;

/* 1-b) tighten the columns */
ALTER TABLE teacher
  MODIFY COLUMN password VARCHAR(255) NOT NULL;

ALTER TABLE parent
  MODIFY COLUMN password VARCHAR(255) NOT NULL;
