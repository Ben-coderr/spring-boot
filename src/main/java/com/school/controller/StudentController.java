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
import java.util.ArrayList;

@RestController
@RequestMapping("/students")
public class StudentController { // endpoints for students

    private final StudentRepository     students;
    private final SchoolClassRepository classes;
    private final StudentService        moves;
    private final AttendanceService     attendanceSvc;
    private final PasswordEncoder       encoder;
    private final UserRepository        users;

    public StudentController(
        StudentRepository     students,
        SchoolClassRepository classes,
        StudentService        moves,
        AttendanceService     attendanceSvc,
        PasswordEncoder       encoder,
        UserRepository        users
    ) {
        this.students      = students;
        this.classes       = classes;
        this.moves         = moves;
        this.attendanceSvc = attendanceSvc;
        this.encoder       = encoder;
        this.users         = users;
    }

    

    @GetMapping
    public List<StudentDto> list() {
        List<Student> all = students.findAll();
        List<StudentDto> out = new ArrayList<>();
        for (Student student : all) {
            out.add(StudentMapper.toDto(student));
        }
        return out;
    }

    @GetMapping("{id}")
    public StudentDto get(@PathVariable Long id) {
        Student student = students.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "student not found"));
        return StudentMapper.toDto(student);
    }

    @GetMapping("{id}/attendance/percentage")
    public Map<String, Object> percentage(@PathVariable Long id) {
        get(id);                                // ensure student exists
        return attendanceSvc.percentForStudent(id);
    }

    //create student

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDto add(@RequestBody Student body) {

       
        if (body.getUser() == null || body.getUser().getPassword() == null)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "password required inside user{}");

        User user = new User();
        String uname = (body.getEmail() != null && !body.getEmail().isBlank())
                     ? body.getEmail()
                     : body.getPhone();

        if (users.findByUsername(uname).isPresent())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "username already exists");

        user.setUsername(uname);
        user.setPassword(encoder.encode(body.getUser().getPassword()));
        user.setRole(Role.STUDENT);

        body.setUser(user);


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
