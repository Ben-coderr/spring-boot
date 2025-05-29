package com.school.dto;

/**
 * Payload for creating a new parent. Only necessary fields are exposed.
 */
public record ParentReq(
        String fullName,
        String phone,
        String email,
        String address,
        String password
) {}
