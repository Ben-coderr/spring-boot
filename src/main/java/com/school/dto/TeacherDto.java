package com.school.dto;

import com.school.model.Teacher;

import java.time.LocalDate;

public record TeacherDto(Long id, String fullName, String email, String phone,
                         String img, String bloodType, String sex,
                         LocalDate birthday, Long subjectId, String placeOfBirth) {
    public static TeacherDto from(Teacher t) {
        Long sid = (t.getSubject() != null) ? t.getSubject().getId() : null;
        return new TeacherDto(t.getId(), t.getFullName(), t.getEmail(), t.getPhone(),
                t.getImg(), t.getBloodType(), t.getSex(), t.getBirthday(), sid,
                t.getPlaceOfBirth());
    }
}
