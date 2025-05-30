package com.school.dto;

import java.time.LocalDate;

//Payload for creating a new student. 

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
