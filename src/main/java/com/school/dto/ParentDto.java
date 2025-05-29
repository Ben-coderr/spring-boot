package com.school.dto;

import com.school.model.Parent;

public record ParentDto(Long id, String fullName, String phone,
                        String email, String address) {
    public static ParentDto from(Parent parent) {
        return new ParentDto(parent.getId(), parent.getFullName(), parent.getPhone(),
                parent.getEmail(), parent.getAddress());
    }
}
