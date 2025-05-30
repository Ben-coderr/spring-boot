package com.school.dto;

import com.school.model.Exam;

import java.time.LocalDate;

public record ExamDto(Long id,
                      String title,
                      LocalDate examDate,
                      Long lessonId,
                      Long studentId) {
    public static ExamDto from(Exam exam) {
        return from(exam, null);
    }

    public static ExamDto from(Exam exam, Long studentId) {
        Long lid = (exam.getLesson() != null) ? exam.getLesson().getId() : null;
        return new ExamDto(exam.getId(), exam.getTitle(), exam.getExamDate(), lid, studentId);
    }
}
