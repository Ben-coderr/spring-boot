package com.school.dto;

import java.time.LocalDate;

public record CreateExamForLessonReq(
        String title,
        LocalDate examDate
) {}
