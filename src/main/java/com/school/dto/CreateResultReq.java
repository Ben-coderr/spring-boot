package com.school.dto;

public record CreateResultReq(
        Long studentId,
        Long examId,
        String kind,
        Double score,
        Boolean isFinal
) {}
