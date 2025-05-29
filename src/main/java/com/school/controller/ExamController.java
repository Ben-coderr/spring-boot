package com.school.controller;

import com.school.model.Exam;
import com.school.repository.ExamRepository;
import com.school.dto.ExamDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/exams")
public class ExamController {

    private final ExamRepository examRepo;
    public ExamController(ExamRepository repo){ this.examRepo = repo; }

    @GetMapping
    public List<ExamDto> allExams(){
        List<Exam> all = examRepo.findAll();
        List<ExamDto> out = new ArrayList<>();
        for (Exam exam : all) {
            out.add(ExamDto.from(exam));
        }
        return out;
    }

    @GetMapping("{id}")
    public ExamDto findExam(@PathVariable Long id){
        Exam exam = examRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"exam "+id+" not found"));
        return ExamDto.from(exam);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExamDto createExam(@RequestBody Exam body){
        if(body.getTitle()==null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.getLesson()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"lesson required");
        return ExamDto.from(examRepo.save(body));
    }

    @PutMapping("{id}")
    public ExamDto updateExam(@PathVariable Long id,@RequestBody Exam in){
        Exam exam = examRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"exam "+id+" not found"));
        if(in.getTitle()!=null)    exam.setTitle(in.getTitle());
        if(in.getExamDate()!=null) exam.setExamDate(in.getExamDate());
        return ExamDto.from(examRepo.save(exam));
    }

    @DeleteMapping("{id}")
    public void removeExam(@PathVariable Long id){ examRepo.deleteById(id); }
}
