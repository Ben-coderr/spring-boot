package com.school.dto;

public record SignupReq( // signup request
        String username,
        String password,
        String fullName,
        String email,
        String phone
) {}
