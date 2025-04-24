package com.school.assignment.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssignmentDTO {
    private Long id;
    private String title;
    private LocalDate dueDate;
    private Long lessonId;
}
