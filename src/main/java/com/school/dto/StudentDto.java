package com.school.dto;

/** What the client gets/produces – no Hibernate, no password */
public record StudentDto(
        Long id,
        String fullName,
        String email,
        SchoolClassDto schoolClass,
        String matricule,
        String placeOfBirth
) {}
