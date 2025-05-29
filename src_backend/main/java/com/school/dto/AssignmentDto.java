package com.school.dto;

import com.school.model.Assignment;

import java.time.LocalDate;

public record AssignmentDto(Long id, String title, LocalDate dueDate, Long lessonId) {
    public static AssignmentDto from(Assignment assignment) {
        Long lid = (assignment.getLesson() != null) ? assignment.getLesson().getId() : null;
        return new AssignmentDto(assignment.getId(), assignment.getTitle(), assignment.getDueDate(), lid);
    }
}
