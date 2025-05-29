package com.school.dto;

import java.time.LocalDate;

/**
 * Payload for creating a new student. Only the fields that can be
 * supplied by the client when registering a student are exposed here.
 */
public record StudentReq(
        String fullName,
        String surname,
        String email,
        String phone,
        Long classId,
        String matricule,
        String placeOfBirth,
        Long parentId,
        String address,
        String img,
        String bloodType,
        String sex,
        LocalDate birthday,
        String password
) {}
