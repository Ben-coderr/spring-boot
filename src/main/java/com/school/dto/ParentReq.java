package com.school.dto;

//Payload for creating a new parent. O

public record ParentReq(
        String fullName,
        String phone,
        String email,
        String address,
        String password
) {}
