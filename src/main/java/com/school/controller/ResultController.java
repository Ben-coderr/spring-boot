package com.school.controller;

import com.school.model.Result;
import com.school.model.Student;
import com.school.model.Exam;
import com.school.repository.ResultRepository;
import com.school.repository.StudentRepository;
import com.school.repository.ExamRepository;
import com.school.dto.ResultDto;
import com.school.dto.CreateResultReq;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.ResultService;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/results")
public class ResultController {

    private final ResultRepository resultRepo;
    private final ResultService resultService;
    private final StudentRepository studentRepo;
    private final ExamRepository examRepo;

    public ResultController(ResultRepository repo, ResultService svc,
                            StudentRepository students,
                            ExamRepository exams) {
        this.resultRepo = repo;
        this.resultService   = svc;
        this.studentRepo = students;
        this.examRepo = exams;
    }


    @GetMapping
    public List<ResultDto> allResults(){
        List<Result> all = resultRepo.findAll();
        List<ResultDto> out = new ArrayList<>();
        for (Result result : all) {
            out.add(ResultDto.from(result));
        }
        return out;
    }

    @GetMapping("{id}")
    public ResultDto get(@PathVariable Long id){
        Result result = resultRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"result "+id+" not found"));
        return ResultDto.from(result);
    }

    @GetMapping("/student/{id}/average")
    public double meanForStudent(@PathVariable Long id) {
        return resultService.avgForStudent(id);
    }

    // GET /results/student/{id}/subject/{sub}/average
    @GetMapping("/student/{id}/subject/{sub}/average")
    public double meanForStudentInSubject(@PathVariable Long id,
                                         @PathVariable("sub") Long subjectId) {
        return resultService.avgForStudentSubject(id, subjectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultDto create(@RequestBody CreateResultReq req){
        if (req.score() == null || req.kind() == null ||
            req.studentId() == null || req.examId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student, exam, score and kind required");

        Student student = studentRepo.findById(req.studentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        Exam exam = examRepo.findById(req.examId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "exam not found"));

        Result result = new Result();
        result.setStudent(student);
        result.setExam(exam);
        result.setKind(req.kind());
        result.setScore(req.score());
        result.setIsFinal(req.isFinal() != null ? req.isFinal() : false);
        return ResultDto.from(resultRepo.save(result));
    }

    @PutMapping("{id}")
    public ResultDto update(@PathVariable Long id,@RequestBody Result in){
        Result result = resultRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"result "+id+" not found"));
        if(in.getScore()!=null) result.setScore(in.getScore());
        if(in.getKind()!=null)  result.setKind(in.getKind());
        return ResultDto.from(resultRepo.save(result));
    }

    @DeleteMapping("{id}")
    public void removeResult(@PathVariable Long id){ resultRepo.deleteById(id); }
}

