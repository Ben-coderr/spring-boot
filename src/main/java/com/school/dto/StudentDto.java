package com.school.dto;

import java.time.LocalDate;

// What the client gets/produces
public record StudentDto(
        Long id,
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
        LocalDate birthday
) {}
