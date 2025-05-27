package com.school.dto;

import com.school.model.Result;

public record ResultDto(Long id, Double score, Long studentId, Long examId) {
    public static ResultDto from(Result r) {
        Long sid = (r.getStudent() != null) ? r.getStudent().getId() : null;
        Long eid = (r.getExam() != null) ? r.getExam().getId() : null;
        return new ResultDto(r.getId(), r.getScore(), sid, eid);
    }
}
