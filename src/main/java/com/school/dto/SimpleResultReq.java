package com.school.dto;

public record SimpleResultReq(Double ccScore,
                              Double examScore,
                              Long subjectId,
                              Long studentId) {}
