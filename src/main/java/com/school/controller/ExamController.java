package com.school.controller;

import com.school.model.Exam;
import com.school.repository.ExamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/exams")
public class ExamController {

    private final ExamRepository exams;
    public ExamController(ExamRepository repo){ exams = repo; }

    @GetMapping
    public List<Exam> list(){ return exams.findAll(); }

    @GetMapping("{id}")
    public Exam get(@PathVariable Long id){
        return exams.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"exam "+id+" not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Exam create(@RequestBody Exam body){
        if(body.getTitle()==null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.getLesson()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"lesson required");
        return exams.save(body);
    }

    @PutMapping("{id}")
    public Exam update(@PathVariable Long id,@RequestBody Exam in){
        Exam e = get(id);
        if(in.getTitle()!=null)    e.setTitle(in.getTitle());
        if(in.getExamDate()!=null) e.setExamDate(in.getExamDate());
        return exams.save(e);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ exams.deleteById(id); }
}
