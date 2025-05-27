package com.school.dto;

import com.school.model.*;
import com.school.repository.SchoolClassRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class StudentMapper {

    public static StudentDto toDto(Student s) {
        Grade g = (s.getSchoolClass() != null) ? s.getSchoolClass().getGrade() : null;
        GradeDto gd = (g == null) ? null : new GradeDto(g.getId(), g.getLevel());

        SchoolClass c = s.getSchoolClass();
        SchoolClassDto cd = (c == null) ? null : new SchoolClassDto(c.getId(), c.getName(), gd);

        return new StudentDto(s.getId(), s.getFullName(), s.getEmail(), cd);
    }

    public static void copyOnWrite(StudentDto dto,
                                   Student target,
                                   SchoolClassRepository classes) {
        if (dto.fullName() != null) target.setFullName(dto.fullName());
        if (dto.email() != null) target.setEmail(dto.email());

        if (dto.schoolClass() != null && dto.schoolClass().id() != null) {
            SchoolClass c = classes.findById(dto.schoolClass().id())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "class not found"));
            target.setSchoolClass(c);
        }
    }

    private StudentMapper() {}
}
