package com.school.service;

import com.school.model.SchoolClass;
import com.school.model.Student;
import com.school.repository.SchoolClassRepository;
import com.school.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService {

    private final StudentRepository    students;
    private final SchoolClassRepository classes;

    public StudentService(StudentRepository s, SchoolClassRepository c) {
        this.students = s;
        this.classes  = c;
    }

    /**
     * Moves one learner into another class if that class still has a free seat.
     * @throws ResponseStatusException 400 if the target class is already full.
     */
    public Student move(Long studentId, Long targetClassId) {

        Student      kid    = students.findById(studentId)
                           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student "+studentId+" ?"));

        SchoolClass  target = classes.findById(targetClassId)
                           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "class "+targetClassId+" ?"));

        long headCount = students.countBySchoolClass_Id(targetClassId);
        Integer room   = target.getCapacity();             // can be null ⇒ unlimited

        if (room != null && headCount >= room)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "class is already full");

        kid.setSchoolClass(target);

        return students.save(kid);
    }
}
