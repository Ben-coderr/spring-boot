package com.school.dto;

import com.school.model.Teacher;

import java.time.LocalDate;

public record TeacherDto(Long id, String fullName, String email, String phone,
                         String img, String bloodType, String sex,
                         LocalDate birthday, Long subjectId, String placeOfBirth) {
    public static TeacherDto from(Teacher teacher) {
        Long sid = (teacher.getSubject() != null) ? teacher.getSubject().getId() : null;
        return new TeacherDto(teacher.getId(), teacher.getFullName(), teacher.getEmail(), teacher.getPhone(),
                teacher.getImg(), teacher.getBloodType(), teacher.getSex(), teacher.getBirthday(), sid,
                teacher.getPlaceOfBirth());
    }
}
