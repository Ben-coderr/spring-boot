package com.school.controller;

import com.school.model.*;
import com.school.repository.*;
import com.school.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/exams")
public class ExamController {

    private final ExamRepository examRepo;
    private final ResultRepository resultRepo;
    public ExamController(ExamRepository repo, ResultRepository resRepo){
        this.examRepo = repo;
        this.resultRepo = resRepo;
    }

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

    @GetMapping("{id}/results")
    public List<ResultDto> resultsForExam(@PathVariable Long id) {
        examRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "exam " + id + " not found"));
        List<Result> all = resultRepo.findByExam_Id(id);
        List<ResultDto> out = new ArrayList<>();
        for (Result r : all) out.add(ResultDto.from(r));
        return out;
    }

    @PostMapping("{id}/results")
    @ResponseStatus(HttpStatus.CREATED)
    public Result createResultForExam(@PathVariable Long id, @RequestBody Result body) {
        Exam exam = examRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "exam " + id + " not found"));
        if (body.getStudent() == null || body.getScore() == null || body.getKind() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student, score and kind required");
        body.setExam(exam);
        if (body.getIsFinal() == null) body.setIsFinal(false);
        return resultRepo.save(body);
    }

    @PutMapping("{eid}/results/{rid}")
    public ResultDto updateResultForExam(@PathVariable("eid") Long examId,
                                         @PathVariable("rid") Long resultId,
                                         @RequestBody Result in) {
        Result result = resultRepo.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "result not found"));
        if (!result.getExam().getId().equals(examId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "result " + resultId + " not for exam " + examId);
        if (in.getScore() != null) result.setScore(in.getScore());
        if (in.getKind() != null) result.setKind(in.getKind());
        return ResultDto.from(resultRepo.save(result));
    }
}
