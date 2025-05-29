package com.school.dto;

import com.school.model.Subject;

public record SubjectDto(Long id, String name, Integer coefficient) {
    public static SubjectDto from(Subject s) {
        return new SubjectDto(s.getId(), s.getName(), s.getCoefficient());
    }
}
