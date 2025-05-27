package com.school.dto;

import com.school.model.Assignment;

import java.time.LocalDate;

public record AssignmentDto(Long id, String title, LocalDate dueDate, Long lessonId) {
    public static AssignmentDto from(Assignment a) {
        Long lid = (a.getLesson() != null) ? a.getLesson().getId() : null;
        return new AssignmentDto(a.getId(), a.getTitle(), a.getDueDate(), lid);
    }
}
