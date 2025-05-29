package com.school.dto;

import com.school.model.Assignment;
import com.school.model.Lesson;
import com.school.repository.LessonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AssignmentMapper {
    public static AssignmentDto toDto(Assignment assignment) {
        return AssignmentDto.from(assignment);
    }

    public static Assignment toEntity(AssignmentDto dto, LessonRepository lessons) {
        Assignment assignment = new Assignment();
        copyOnWrite(dto, assignment, lessons);
        return assignment;
    }

    public static void copyOnWrite(AssignmentDto dto,
                                   Assignment target,
                                   LessonRepository lessons) {
        if (dto.title() != null) target.setTitle(dto.title());
        if (dto.dueDate() != null) target.setDueDate(dto.dueDate());

        if (dto.lessonId() != null) {
            Lesson lesson = lessons.findById(dto.lessonId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "lesson not found"));
            target.setLesson(lesson);
        }
    }

    private AssignmentMapper() {}
}
