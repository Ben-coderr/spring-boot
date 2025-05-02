CREATE TABLE event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    starts_at DATETIME,
    ends_at   DATETIME,
    class_id BIGINT,
    CONSTRAINT fk_event_class FOREIGN KEY (class_id) REFERENCES school_class(id)
);
