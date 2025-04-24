package com.yourorg.school.teacher.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeacherDTO {
    private Long id;
    private String fullName;
    private String email;
}
