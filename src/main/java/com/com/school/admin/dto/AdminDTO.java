package com.com.school.admin.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminDTO {
    private Long id;
    private String fullName;
    private String email;
}
