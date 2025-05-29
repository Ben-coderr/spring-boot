package com.school.dto;

import com.school.model.*;
import com.school.repository.SchoolClassRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class StudentMapper {

    public static StudentDto toDto(Student student) {
        Grade grade = (student.getSchoolClass() != null) ? student.getSchoolClass().getGrade() : null;
        GradeDto gradeDto = (grade == null) ? null : new GradeDto(grade.getId(), grade.getLevel());

        SchoolClass classEntity = student.getSchoolClass();
        SchoolClassDto classDto = (classEntity == null) ? null : new SchoolClassDto(classEntity.getId(), classEntity.getName(), gradeDto);

        return new StudentDto(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                classDto,
                student.getMatricule(),
                student.getPlaceOfBirth()
        );
    }

    public static void copyOnWrite(StudentDto dto,
                                   Student target,
                                   SchoolClassRepository classes) {
        if (dto.fullName() != null) target.setFullName(dto.fullName());
        if (dto.email() != null) target.setEmail(dto.email());
        if (dto.matricule() != null) target.setMatricule(dto.matricule());
        if (dto.placeOfBirth() != null) target.setPlaceOfBirth(dto.placeOfBirth());

        if (dto.schoolClass() != null && dto.schoolClass().id() != null) {
            SchoolClass classEntity = classes.findById(dto.schoolClass().id())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "class not found"));
            target.setSchoolClass(classEntity);
        }
    }

    private StudentMapper() {}
}
