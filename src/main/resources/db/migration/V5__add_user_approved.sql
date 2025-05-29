ALTER TABLE users
  ADD COLUMN approved TINYINT(1) NOT NULL DEFAULT 0;

-- make sure the hardcoded admin can log in
UPDATE users SET approved = 1
WHERE username = 'admin';
