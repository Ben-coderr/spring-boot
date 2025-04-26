ALTER TABLE lesson
    ADD COLUMN day        VARCHAR(10)  NOT NULL DEFAULT 'MONDAY',
    ADD COLUMN start_time TIME         NOT NULL DEFAULT '08:00:00',
    ADD COLUMN end_time   TIME         NOT NULL DEFAULT '09:00:00';

-- Optional: keep queries quick
CREATE INDEX idx_lesson_day_start ON lesson(day, start_time);
