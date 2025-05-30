package com.school.dto;

import com.school.model.Result;

/** Simple result view */
public record SimpleResultDto(Long id, Double ccScore, Double examScore,
                              Long subjectId, Long studentId) {
    public static SimpleResultDto from(Result result) {
        Long sid = (result.getStudent() != null) ? result.getStudent().getId() : null;
        Long sub = (result.getSubject() != null) ? result.getSubject().getId() : null;
        return new SimpleResultDto(result.getId(), result.getCcScore(),
                result.getExamScore(), sub, sid);
    }
}
