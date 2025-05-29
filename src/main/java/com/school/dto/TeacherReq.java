package com.school.dto;

import java.time.LocalDate;

/**
 * Payload for creating a new teacher. Only the fields needed when registering
 * a teacher are exposed here.
 */
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
