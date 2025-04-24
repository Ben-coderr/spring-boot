package com.school.lesson.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LessonDTO {
    private Long id;
    private String topic;
    private LocalDate lessonDate;
    private Long subjectId;
    private Long teacherId;
    private Long classId;
}
