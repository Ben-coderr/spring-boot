package com.school.dto;

import com.school.model.Admin;

public record AdminDto(Long id, String fullName, String email) {
    public static AdminDto from(Admin a) {
        return new AdminDto(a.getId(), a.getFullName(), a.getEmail());
    }
}
