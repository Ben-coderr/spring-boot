package com.school.dto;

import java.time.LocalDate;

// Payload for creating a new teacher.
public record TeacherReq(
        String fullName,
        String email,
        String phone,
        String placeOfBirth,
        Long subjectId,
        String img,
        String bloodType,
        String sex,
        LocalDate birthday,
        String password
) {}
