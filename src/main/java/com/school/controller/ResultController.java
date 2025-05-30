package com.school.controller;

import com.school.model.Result;
import com.school.model.Student;
import com.school.model.Exam;
import com.school.repository.ResultRepository;
import com.school.repository.StudentRepository;
import com.school.repository.ExamRepository;
import com.school.repository.SubjectRepository;
import com.school.model.Subject;
import com.school.dto.SimpleResultReq;
import com.school.dto.ResultDto;
import com.school.dto.SimpleResultDto;
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
    private final SubjectRepository subjectRepo;

    public ResultController(ResultRepository repo, ResultService svc,
                            StudentRepository students,
                            ExamRepository exams,
                            SubjectRepository subjects) {
        this.resultRepo = repo;
        this.resultService   = svc;
        this.studentRepo = students;
        this.examRepo = exams;
        this.subjectRepo = subjects;
    }


    @GetMapping
    public List<SimpleResultDto> allResults(){
        List<Result> all = resultRepo.findAll();
        List<SimpleResultDto> out = new ArrayList<>();
        for (Result result : all) {
            out.add(SimpleResultDto.from(result));
        }
        return out;
    }

    @GetMapping("{id}")
    public ResultDto get(@PathVariable Long id){
        Result result = resultRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"result "+id+" not found"));
        return ResultDto.from(result);
    }

    // list results for one student
    @GetMapping("/student/{sid}")
    public List<SimpleResultDto> byStudent(@PathVariable("sid") Long id) {
        List<Result> all = resultRepo.findByStudent_Id(id);
        List<SimpleResultDto> out = new ArrayList<>();
        for (Result r : all) out.add(SimpleResultDto.from(r));
        return out;
    }

    // list results for one subject
    @GetMapping("/subject/{sub}")
    public List<SimpleResultDto> bySubject(@PathVariable("sub") Long id) {
        List<Result> all = resultRepo.findBySubject_Id(id);
        List<SimpleResultDto> out = new ArrayList<>();
        for (Result r : all) out.add(SimpleResultDto.from(r));
        return out;
    }

    // list results for student and subject
    @GetMapping("/student/{sid}/subject/{sub}")
    public List<SimpleResultDto> byStudentSubject(@PathVariable("sid") Long sid,
                                                 @PathVariable("sub") Long sub) {
        List<Result> all = resultRepo.findByStudent_IdAndSubject_Id(sid, sub);
        List<SimpleResultDto> out = new ArrayList<>();
        for (Result r : all) out.add(SimpleResultDto.from(r));
        return out;
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
    public ResultDto create(@RequestBody ResultDto dto){
        if (dto.score() == null || dto.kind() == null ||
            dto.studentId() == null || dto.examId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student, exam, score and kind required");

        Student student = studentRepo.findById(dto.studentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        Exam exam = examRepo.findById(dto.examId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "exam not found"));

        Result result = new Result();
        result.setStudent(student);
        result.setExam(exam);
        result.setSubject(exam.getLesson() != null ? exam.getLesson().getSubject() : null);
        result.setKind(dto.kind());
        result.setScore(dto.score());
        if ("CC".equalsIgnoreCase(dto.kind())) result.setCcScore(dto.score());
        if ("EXAM".equalsIgnoreCase(dto.kind())) result.setExamScore(dto.score());
        result.setIsFinal(Boolean.FALSE);
        return ResultDto.from(resultRepo.save(result));
    }

    @PutMapping("{id}")
    public ResultDto update(@PathVariable Long id, @RequestBody ResultDto in){
        Result result = resultRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"result "+id+" not found"));
        if(in.score()!=null) result.setScore(in.score());
        if(in.kind()!=null)  result.setKind(in.kind());
        if("CC".equalsIgnoreCase(in.kind())) result.setCcScore(in.score());
        if("EXAM".equalsIgnoreCase(in.kind())) result.setExamScore(in.score());
        return ResultDto.from(resultRepo.save(result));
    }

    @DeleteMapping("{id}")
    public void removeResult(@PathVariable Long id){ resultRepo.deleteById(id); }

    // simplified endpoints using SimpleResultReq payload
    @PostMapping("/result")
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleResultDto createSimple(@RequestBody SimpleResultReq body) {
        if (body.studentId() == null || body.subjectId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "studentId and subjectId required");
        Student student = studentRepo.findById(body.studentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        Subject subject = subjectRepo.findById(body.subjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "subject not found"));
        Result r = new Result();
        r.setStudent(student);
        r.setSubject(subject);
        r.setCcScore(body.ccScore());
        r.setExamScore(body.examScore());
        r.setIsFinal(Boolean.FALSE);
        return SimpleResultDto.from(resultRepo.save(r));
    }

    @PutMapping("/result/{id}")
    public SimpleResultDto updateSimple(@PathVariable Long id,
                                        @RequestBody SimpleResultReq in) {
        Result r = resultRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "result " + id + " not found"));
        if (in.ccScore() != null) r.setCcScore(in.ccScore());
        if (in.examScore() != null) r.setExamScore(in.examScore());
        if (in.studentId() != null) {
            Student student = studentRepo.findById(in.studentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
            r.setStudent(student);
        }
        if (in.subjectId() != null) {
            Subject subject = subjectRepo.findById(in.subjectId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "subject not found"));
            r.setSubject(subject);
        }
        return SimpleResultDto.from(resultRepo.save(r));
    }
}

