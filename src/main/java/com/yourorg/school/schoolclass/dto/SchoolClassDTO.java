package com.yourorg.school.schoolclass.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolClassDTO {
    private Long id;
    private String name;
    private Long gradeId;
}
