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

        Parent parentEntity = student.getParent();
        ParentDto parentDto = (parentEntity == null) ? null : ParentDto.from(parentEntity);

        return new StudentDto(
                student.getId(),
                student.getFullName(),
                student.getSurname(),
                student.getEmail(),
                student.getPhone(),
                classDto,
                student.getMatricule(),
                student.getPlaceOfBirth(),
                parentDto,
                student.getAddress(),
                student.getImg(),
                student.getBloodType(),
                student.getSex(),
                student.getBirthday()
        );
    }

    public static void copyOnWrite(StudentDto dto,
                                   Student target,
                                   SchoolClassRepository classes) {
        if (dto.fullName() != null) target.setFullName(dto.fullName());
        if (dto.surname() != null) target.setSurname(dto.surname());
        if (dto.email() != null) target.setEmail(dto.email());
        if (dto.phone() != null) target.setPhone(dto.phone());
        if (dto.matricule() != null) target.setMatricule(dto.matricule());
        if (dto.placeOfBirth() != null) target.setPlaceOfBirth(dto.placeOfBirth());
        if (dto.address() != null) target.setAddress(dto.address());
        if (dto.img() != null) target.setImg(dto.img());
        if (dto.bloodType() != null) target.setBloodType(dto.bloodType());
        if (dto.sex() != null) target.setSex(dto.sex());
        if (dto.birthday() != null) target.setBirthday(dto.birthday());

        if (dto.schoolClass() != null && dto.schoolClass().id() != null) {
            SchoolClass classEntity = classes.findById(dto.schoolClass().id())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "class not found"));
            target.setSchoolClass(classEntity);
        }
    }

    private StudentMapper() {}
}
