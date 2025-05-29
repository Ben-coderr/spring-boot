CREATE TABLE users (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role     VARCHAR(50)
);

-- default adming for testing
INSERT INTO users(username, password, role) VALUES
('admin', 'password', 'ADMIN');

ALTER TABLE parent ADD password VARCHAR(255) NOT NULL;
