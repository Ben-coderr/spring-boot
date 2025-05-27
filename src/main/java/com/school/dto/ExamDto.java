package com.school.dto;

import com.school.model.Exam;

import java.time.LocalDate;

public record ExamDto(Long id, String title, LocalDate examDate, Long lessonId) {
    public static ExamDto from(Exam e) {
        Long lid = (e.getLesson() != null) ? e.getLesson().getId() : null;
        return new ExamDto(e.getId(), e.getTitle(), e.getExamDate(), lid);
    }
}
