package com.school.dto;

import com.school.model.Exam;

import java.time.LocalDate;

public record ExamDto(Long id, String title, LocalDate examDate, Long lessonId) {
    public static ExamDto from(Exam exam) {
        Long lid = (exam.getLesson() != null) ? exam.getLesson().getId() : null;
        return new ExamDto(exam.getId(), exam.getTitle(), exam.getExamDate(), lid);
    }
}
