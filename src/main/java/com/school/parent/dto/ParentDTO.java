package com.school.parent.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParentDTO {
    private Long id;
    private String fullName;
    private String email;
}
