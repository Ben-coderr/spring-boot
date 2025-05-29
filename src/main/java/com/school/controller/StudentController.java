package com.school.controller;

import com.school.dto.*;
import com.school.model.Role;
import com.school.model.Student;
import com.school.model.User;
import com.school.repository.*;
import com.school.service.AttendanceService;
import com.school.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder       encoder;

    public StudentController(
        StudentRepository     students,
        SchoolClassRepository classes,
        StudentService        moves,
        AttendanceService     attendanceSvc,
        PasswordEncoder       encoder
    ) {   
        this.students      = students;
        this.classes       = classes;
        this.moves         = moves;
        this.attendanceSvc = attendanceSvc;
        this.encoder       = encoder;
    }

    

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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "student not found"));
        return StudentMapper.toDto(s);
    }

    @GetMapping("{id}/attendance/percentage")
    public Map<String, Object> percentage(@PathVariable Long id) {
        get(id);                                // ensure student exists
        return attendanceSvc.percentForStudent(id);
    }

    /* -------------------- create -------------------- */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDto add(@RequestBody Student body) {

       
        if (body.getUser() == null || body.getUser().getPassword() == null)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "password required inside user{}");

        User u = new User();
        String uname = (body.getEmail() != null && !body.getEmail().isBlank())
                     ? body.getEmail()
                     : body.getPhone();
        u.setUsername(uname);
        u.setPassword(encoder.encode(body.getUser().getPassword()));
        u.setRole(Role.STUDENT);

        body.setUser(u);


        return StudentMapper.toDto(students.save(body));  
    }



    @PutMapping("{id}")
    public StudentDto update(@PathVariable Long id,
                             @RequestBody StudentDto in) {

        Student entity = students.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "student not found"));

        StudentMapper.copyOnWrite(in, entity, classes);
        return StudentMapper.toDto(students.save(entity));
    }



    @PutMapping("{id}/class/{targetId}")
    public StudentDto reclass(@PathVariable Long id,
                              @PathVariable("targetId") Long newClass) {
        return StudentMapper.toDto(moves.move(id, newClass));
    }



    @DeleteMapping("{id}")
    public void deleteStudent(@PathVariable Long id) {
        students.deleteById(id);
    }
}
