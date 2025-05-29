package com.school.controller;

import com.school.model.Role;
import com.school.model.Teacher;
import com.school.model.User;
import com.school.repository.TeacherRepository;
import com.school.dto.ParentDto;
import com.school.dto.TeacherDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    private static void need(String v, String f) {
        if (v == null || v.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, f + " required");
    }


    @GetMapping
    public List<TeacherDto> listTeachers() {
        return teachers.findAll().stream().map(TeacherDto::from).toList();
    }

    @GetMapping("{id}")
    public TeacherDto getTeacher(@PathVariable Long id) {
        Teacher t = teachers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        return TeacherDto.from(t);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherDto add(@RequestBody Teacher body){

        need(body.getFullName(),"name");
        need(body.getUser() != null ? body.getUser().getPassword() : null,
            "password");

        // build the user (username = email if present, else phone)
        User u = new User();
        String uname = (body.getEmail() != null && !body.getEmail().isBlank())
                    ? body.getEmail()
                    : body.getPhone();
        u.setUsername(uname);
        u.setPassword(encoder.encode(body.getUser().getPassword()));
        u.setRole(Role.TEACHER);
        body.setUser(u);

        return TeacherDto.from(teachers.save(body));
    }

    @PutMapping("{id}")
    public TeacherDto updateTeacher(@PathVariable Long id, @RequestBody Teacher in) {

        Teacher t = teachers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));

        if (in.getFullName() != null) t.setFullName(in.getFullName());

        if (in.getEmail() != null) {
            if (!in.getEmail().contains("@"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email");
            t.setEmail(in.getEmail());
        }

        // if (in.getPassword() != null) t.setPassword(in.getPassword());
        if (in.getSubject()  != null) t.setSubject(in.getSubject());

        return TeacherDto.from(teachers.save(t));
    }

    @DeleteMapping("{id}")
    public void removeTeacher(@PathVariable Long id) {
        teachers.deleteById(id);
    }
}
