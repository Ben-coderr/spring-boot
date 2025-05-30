package com.school.dto;

import com.school.model.Assignment;

import java.time.LocalDate;

public record AssignmentDto(Long id,
                            String title,
                            LocalDate dueDate,
                            Long lessonId,
                            Long studentId) {
    public static AssignmentDto from(Assignment assignment) {
        return from(assignment, null);
    }

    public static AssignmentDto from(Assignment assignment, Long studentId) {
        Long lid = (assignment.getLesson() != null) ? assignment.getLesson().getId() : null;
        return new AssignmentDto(assignment.getId(), assignment.getTitle(), assignment.getDueDate(), lid, studentId);
    }
}
