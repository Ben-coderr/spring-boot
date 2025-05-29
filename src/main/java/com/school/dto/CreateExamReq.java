package com.school.dto;

import java.time.LocalDate;

public record CreateExamReq(
        String title,
        LocalDate examDate,
        Long lessonId
) {}
