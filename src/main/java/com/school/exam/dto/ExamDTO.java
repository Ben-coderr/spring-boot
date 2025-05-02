package com.school.exam.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExamDTO {
    private Long id;
    private String title;
    private LocalDate examDate;
    private Long lessonId;
}
