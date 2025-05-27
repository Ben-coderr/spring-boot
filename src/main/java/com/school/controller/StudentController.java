package com.school.controller;

import com.school.dto.*;
import com.school.model.Student;
import com.school.repository.*;
import com.school.service.AttendanceService;
import com.school.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository     students;
    private final SchoolClassRepository classes;
    private final StudentService        moves;
    private final AttendanceService     attendanceSvc;

    public StudentController(StudentRepository     students,
                             SchoolClassRepository classes,
                             StudentService        moves,
                             AttendanceService     attendanceSvc) {
        this.students      = students;
        this.classes       = classes;
        this.moves         = moves;
        this.attendanceSvc = attendanceSvc;
    }

    //Methods
    @GetMapping
    public List<StudentDto> list() {
        return students.findAll()
                       .stream()
                       .map(StudentMapper::toDto)
                       .toList();
    }

    @GetMapping("{id}")
    public StudentDto get(@PathVariable Long id) {
        Student s = students.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        return StudentMapper.toDto(s);
    }

    @GetMapping("{id}/attendance/percentage")
    public Map<String, Object> percentage(@PathVariable Long id) {
        get(id);                       // ensures student exists
        return attendanceSvc.percentForStudent(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDto create(@RequestBody StudentDto body) {

        Student newEntity = new Student();
        StudentMapper.copyOnWrite(body, newEntity, classes);
        return StudentMapper.toDto(students.save(newEntity));
    }

    @PutMapping("{id}")
    public StudentDto update(@PathVariable Long id,
                             @RequestBody StudentDto in) {

        Student entity = students.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));

        StudentMapper.copyOnWrite(in, entity, classes);
        return StudentMapper.toDto(students.save(entity));
    }

    @PutMapping("{id}/class/{targetId}")
    public StudentDto reclass(
            @PathVariable Long id,
            @PathVariable("targetId") Long newClass) {

        return StudentMapper.toDto(moves.move(id, newClass));
    }

    @DeleteMapping("{id}")
    public void deleteStudent(@PathVariable Long id) {
        students.deleteById(id);
    }
}
