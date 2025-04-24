package com.school.student.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentDTO {
    private Long id;
    private String fullName;
    private String email;
}
