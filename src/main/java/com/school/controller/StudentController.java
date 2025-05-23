package com.school.controller;

import com.school.model.Student;
import com.school.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.StudentService;
import com.school.service.AttendanceService;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService moves;
    private final AttendanceService attendanceSvc;
    private final StudentRepository students;
    
    public StudentController(StudentRepository repo, StudentService svc, AttendanceService as) {  
        this.students = repo;
        this.moves    = svc;
        this.attendanceSvc = as;
    }

   //method for validating informations
    private static void require(String value, String field) {
        if (value == null || value.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " required");
    }



    //Methods
    @GetMapping
    public List<Student> listStudents() {
        return students.findAll();
    }

    @GetMapping("{id}")
    public Student getStudent(@PathVariable Long id) {
        return students.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student " + id + " not found"));
    }

    @GetMapping("{id}/attendance/percentage")
    public Map<String, Object> percentage(@PathVariable Long id) {
        getStudent(id);                       // ensures student exists
        return attendanceSvc.percentForStudent(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@RequestBody Student body) {

        require(body.getFullName(), "name");
        require(body.getEmail(),     "email");
        require(body.getPassword(),  "password");

        if (!body.getEmail().contains("@"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email");

        if (body.getSchoolClass() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "class required");

        return students.save(body);
    }

    @PutMapping("{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student in) {

        Student s = getStudent(id);

        if (in.getFullName() != null)  s.setFullName(in.getFullName());
        if (in.getSurname()  != null)  s.setSurname(in.getSurname());
        if (in.getEmail()    != null) {
            if (!in.getEmail().contains("@"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email");
            s.setEmail(in.getEmail());
        }
        if (in.getPassword() != null)  s.setPassword(in.getPassword());
        if (in.getSchoolClass()!=null) s.setSchoolClass(in.getSchoolClass());

        return students.save(s);
    }

    @PutMapping("{id}/class/{targetId}")
    public Student reclass(
            @PathVariable Long id,
            @PathVariable("targetId") Long newClass) {

        return moves.move(id, newClass);
    }

    @DeleteMapping("{id}")
    public void deleteStudent(@PathVariable Long id) {
        students.deleteById(id);
    }
}
