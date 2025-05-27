package com.school.controller;

import com.school.model.Exam;
import com.school.repository.ExamRepository;
import com.school.dto.ExamDto;
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
    public List<ExamDto> list(){
        return exams.findAll().stream().map(ExamDto::from).toList();
    }

    @GetMapping("{id}")
    public ExamDto get(@PathVariable Long id){
        Exam e = exams.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"exam "+id+" not found"));
        return ExamDto.from(e);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExamDto create(@RequestBody Exam body){
        if(body.getTitle()==null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.getLesson()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"lesson required");
        return ExamDto.from(exams.save(body));
    }

    @PutMapping("{id}")
    public ExamDto update(@PathVariable Long id,@RequestBody Exam in){
        Exam e = exams.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"exam "+id+" not found"));
        if(in.getTitle()!=null)    e.setTitle(in.getTitle());
        if(in.getExamDate()!=null) e.setExamDate(in.getExamDate());
        return ExamDto.from(exams.save(e));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ exams.deleteById(id); }
}
