package com.school.dto;

public record SignupReq(
        String username,
        String password,
        String fullName,
        String email,
        String phone
) {}
