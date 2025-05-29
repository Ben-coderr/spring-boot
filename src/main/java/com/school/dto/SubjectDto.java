package com.school.dto;

import com.school.model.Subject;

public record SubjectDto(Long id, String name, Integer coefficient) {
    public static SubjectDto from(Subject subject) {
        return new SubjectDto(subject.getId(), subject.getName(), subject.getCoefficient());
    }
}
