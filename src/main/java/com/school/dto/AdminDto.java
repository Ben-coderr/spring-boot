package com.school.dto;

import com.school.model.Admin;

public record AdminDto(Long id, String fullName, String email) { // admin data
    public static AdminDto from(Admin admin) {
        return new AdminDto(admin.getId(), admin.getFullName(), admin.getEmail());
    }
}
