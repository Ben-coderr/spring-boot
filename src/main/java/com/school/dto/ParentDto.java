package com.school.dto;

import com.school.model.Parent;

public record ParentDto(Long id, String fullName, String phone,
                        String email, String address) {
    public static ParentDto from(Parent p) {
        return new ParentDto(p.getId(), p.getFullName(), p.getPhone(),
                p.getEmail(), p.getAddress());
    }
}
