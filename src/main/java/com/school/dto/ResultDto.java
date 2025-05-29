package com.school.dto;

import com.school.model.Result;

public record ResultDto(Long id, Double score, Long studentId, Long examId) {
    public static ResultDto from(Result result) {
        Long sid = (result.getStudent() != null) ? result.getStudent().getId() : null;
        Long eid = (result.getExam() != null) ? result.getExam().getId() : null;
        return new ResultDto(result.getId(), result.getScore(), sid, eid);
    }
}
