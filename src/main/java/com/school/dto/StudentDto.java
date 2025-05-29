package com.school.dto;

import java.time.LocalDate;

// What the client gets/produces – no Hibernate, no password
public record StudentDto(
        Long id,
        String fullName,
        String surname,
        String email,
        String phone,
        SchoolClassDto schoolClass,
        String matricule,
        String placeOfBirth,
        ParentDto parent,
        String address,
        String img,
        String bloodType,
        String sex,
        LocalDate birthday
) {}
