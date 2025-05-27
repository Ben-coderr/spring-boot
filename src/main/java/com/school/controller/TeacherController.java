package com.school.controller;

import com.school.model.Teacher;
import com.school.repository.TeacherRepository;
import com.school.dto.TeacherDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherRepository teachers;

    public TeacherController(TeacherRepository repo) {
        this.teachers = repo;
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
    public TeacherDto addTeacher(@RequestBody Teacher body) {

        need(body.getFullName(), "name");
        need(body.getPassword(), "password");

        if (body.getSubject() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "subject required");

        if (body.getEmail() != null && !body.getEmail().contains("@"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email");

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

        if (in.getPassword() != null) t.setPassword(in.getPassword());
        if (in.getSubject()  != null) t.setSubject(in.getSubject());

        return TeacherDto.from(teachers.save(t));
    }

    @DeleteMapping("{id}")
    public void removeTeacher(@PathVariable Long id) {
        teachers.deleteById(id);
    }
}
