CREATE TABLE announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    published_at DATETIME NOT NULL,
    class_id BIGINT,
    CONSTRAINT fk_announcement_class FOREIGN KEY (class_id) REFERENCES school_class(id)
);
