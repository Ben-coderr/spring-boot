package com.school.controller;

import com.school.model.*;
import com.school.repository.*;
import com.school.dto.TeacherDto;
import com.school.dto.TeacherReq;
import com.school.dto.TeacherMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/teachers")
public class TeacherController { // teacher endpoints

    private final TeacherRepository teacherRepo;
    private final PasswordEncoder   passwordEncoder;
    private final UserRepository    userRepo;
    private final SubjectRepository subjectRepo;

    public TeacherController(TeacherRepository repo,
                             PasswordEncoder    passwordEncoder,
                             UserRepository     userRepo,
                             SubjectRepository  subjectRepo) {
        this.teacherRepo = repo;
        this.passwordEncoder  = passwordEncoder;
        this.userRepo    = userRepo;
        this.subjectRepo = subjectRepo;
    }

    //helper to validate needed fields
    private static void need(String value, String field) {
        if (value == null || value.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " required");
    }


    @GetMapping
    public List<TeacherDto> listTeachers() {
        List<Teacher> all = teacherRepo.findAll();
        List<TeacherDto> out = new ArrayList<>();
        for (Teacher teacher : all) {
            out.add(TeacherDto.from(teacher));
        }
        return out;
    }

    @GetMapping("{id}")
    public TeacherDto getTeacher(@PathVariable Long id) {
        Teacher teacher = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        return TeacherDto.from(teacher);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherDto registerTeacher(@RequestBody TeacherReq body){

        need(body.fullName(),"name");
        need(body.password(),"password");

        User user = new User();
        String uname = (body.email() != null && !body.email().isBlank())
                    ? body.email()
                    : body.phone();

        if (userRepo.findByUsername(uname).isPresent())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "username already exists");

        user.setUsername(uname);
        user.setPassword(passwordEncoder.encode(body.password()));
        user.setRole(Role.TEACHER);

        Teacher entity = new Teacher();
        entity.setFullName(body.fullName());
        entity.setEmail(body.email());
        entity.setPhone(body.phone());
        entity.setPlaceOfBirth(body.placeOfBirth());
        entity.setUser(user);
        entity.setImg(body.img());
        entity.setBloodType(body.bloodType());
        entity.setSex(body.sex());
        entity.setBirthday(body.birthday());

        if (body.subjectId() != null) {
            Subject subject = subjectRepo.findById(body.subjectId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "subject not found"));
            entity.setSubject(subject);
        }

        return TeacherDto.from(teacherRepo.save(entity));
    }

    @PutMapping("{id}")
    public TeacherDto updateTeacher(@PathVariable Long id, @RequestBody TeacherDto in) {

        Teacher teacher = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));

        TeacherMapper.copyOnWrite(in, teacher, subjectRepo);
        return TeacherMapper.toDto(teacherRepo.save(teacher));
    }

    @DeleteMapping("{id}")
    public void removeTeacher(@PathVariable Long id) {
        teacherRepo.deleteById(id);
    }
}
