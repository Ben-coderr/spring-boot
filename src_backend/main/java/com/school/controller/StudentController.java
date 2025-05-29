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
public class StudentController {

    private final StudentRepository     studentRepo;
    private final SchoolClassRepository classRepo;
    private final StudentService        studentService;
    private final AttendanceService     attendanceService;
    private final PasswordEncoder       passwordEncoder;
    private final UserRepository        userRepo;

    public StudentController(
        StudentRepository     studentRepo,
        SchoolClassRepository classRepo,
        StudentService        service,
        AttendanceService     attendance,
        PasswordEncoder       passwordEncoder,
        UserRepository        userRepo
    ) {
        this.studentRepo        = studentRepo;
        this.classRepo          = classRepo;
        this.studentService     = service;
        this.attendanceService  = attendance;
        this.passwordEncoder    = passwordEncoder;
        this.userRepo           = userRepo;
    }

    

    @GetMapping
    public List<StudentDto> allStudents() {
        List<Student> all = studentRepo.findAll();
        List<StudentDto> out = new ArrayList<>();
        for (Student student : all) {
            out.add(StudentMapper.toDto(student));
        }
        return out;
    }

    @GetMapping("{id}")
    public StudentDto findStudent(@PathVariable Long id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "student not found"));
        return StudentMapper.toDto(student);
    }

    @GetMapping("{id}/attendance/percentage")
    public Map<String, Object> attendancePercentage(@PathVariable Long id) {
        findStudent(id);                                // ensure student exists
        return attendanceService.percentForStudent(id);
    }

    //create student

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDto registerStudent(@RequestBody Student body) {

       
        if (body.getUser() == null || body.getUser().getPassword() == null)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "password required inside user{}");

        User user = new User();
        String uname = (body.getEmail() != null && !body.getEmail().isBlank())
                     ? body.getEmail()
                     : body.getPhone();

        if (userRepo.findByUsername(uname).isPresent())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "username already exists");

        user.setUsername(uname);
        user.setPassword(passwordEncoder.encode(body.getUser().getPassword()));
        user.setRole(Role.STUDENT);

        body.setUser(user);


        return StudentMapper.toDto(studentRepo.save(body));  
    }



    @PutMapping("{id}")
    public StudentDto updateStudent(@PathVariable Long id,
                             @RequestBody StudentDto in) {

        Student entity = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "student not found"));

        StudentMapper.copyOnWrite(in, entity, classRepo);
        return StudentMapper.toDto(studentRepo.save(entity));
    }



    @PutMapping("{id}/class/{targetId}")
    public StudentDto changeClass(@PathVariable Long id,
                                  @PathVariable("targetId") Long newClass) {
        return StudentMapper.toDto(studentService.move(id, newClass));
    }



    @DeleteMapping("{id}")
    public void removeStudent(@PathVariable Long id) {
        studentRepo.deleteById(id);
    }
}
