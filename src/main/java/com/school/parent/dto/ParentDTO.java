package com.school.parent.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParentDTO {
    private Long id;
    private String fullName;
    private String email;
    private Set<Long> childIds;
}
