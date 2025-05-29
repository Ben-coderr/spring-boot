package com.school.dto;

import com.school.model.Teacher;
import com.school.model.Subject;
import com.school.repository.SubjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class TeacherMapper {
    public static TeacherDto toDto(Teacher teacher) {
        return TeacherDto.from(teacher);
    }

    public static void copyOnWrite(TeacherDto dto,
                                   Teacher target,
                                   SubjectRepository subjects) {
        if (dto.fullName() != null) target.setFullName(dto.fullName());
        if (dto.email() != null) target.setEmail(dto.email());
        if (dto.phone() != null) target.setPhone(dto.phone());
        if (dto.placeOfBirth() != null) target.setPlaceOfBirth(dto.placeOfBirth());
        if (dto.img() != null) target.setImg(dto.img());
        if (dto.bloodType() != null) target.setBloodType(dto.bloodType());
        if (dto.sex() != null) target.setSex(dto.sex());
        if (dto.birthday() != null) target.setBirthday(dto.birthday());

        if (dto.subjectId() != null) {
            Subject subject = subjects.findById(dto.subjectId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "subject not found"));
            target.setSubject(subject);
        }
    }

    private TeacherMapper() {}
}
