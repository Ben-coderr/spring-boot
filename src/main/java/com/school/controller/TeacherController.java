package com.school.controller;

import com.school.model.Role;
import com.school.model.Teacher;
import com.school.model.User;
import com.school.repository.TeacherRepository;
import com.school.dto.TeacherDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherRepository teachers;
    private final PasswordEncoder encoder;

    public TeacherController(TeacherRepository repo,
                             PasswordEncoder   encoder) {
        this.teachers = repo;
        this.encoder  = encoder;
    }

    //helper to validate needed fields
    private static void need(String value, String field) {
        if (value == null || value.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " required");
    }


    @GetMapping
    public List<TeacherDto> listTeachers() {
        List<Teacher> all = teachers.findAll();
        List<TeacherDto> out = new ArrayList<>();
        for (Teacher teacher : all) {
            out.add(TeacherDto.from(teacher));
        }
        return out;
    }

    @GetMapping("{id}")
    public TeacherDto getTeacher(@PathVariable Long id) {
        Teacher teacher = teachers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        return TeacherDto.from(teacher);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherDto add(@RequestBody Teacher body){

        need(body.getFullName(),"name");
        need(body.getUser() != null ? body.getUser().getPassword() : null,
            "password");

        // build the user (username = email if present, else phone)
        User user = new User();
        String uname = (body.getEmail() != null && !body.getEmail().isBlank())
                    ? body.getEmail()
                    : body.getPhone();
        user.setUsername(uname);
        user.setPassword(encoder.encode(body.getUser().getPassword()));
        user.setRole(Role.TEACHER);
        body.setUser(user);

        return TeacherDto.from(teachers.save(body));
    }

    @PutMapping("{id}")
    public TeacherDto updateTeacher(@PathVariable Long id, @RequestBody Teacher in) {

        Teacher teacher = teachers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));

        if (in.getFullName() != null) teacher.setFullName(in.getFullName());

        if (in.getEmail() != null) {
            if (!in.getEmail().contains("@"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email");
            teacher.setEmail(in.getEmail());
        }

        // if (in.getPassword() != null) teacher.setPassword(in.getPassword());
        if (in.getSubject()  != null) teacher.setSubject(in.getSubject());

        return TeacherDto.from(teachers.save(teacher));
    }

    @DeleteMapping("{id}")
    public void removeTeacher(@PathVariable Long id) {
        teachers.deleteById(id);
    }
}
