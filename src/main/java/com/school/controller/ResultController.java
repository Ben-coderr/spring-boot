package com.school.controller;

import com.school.model.Result;
import com.school.repository.ResultRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.ResultService;

import java.util.List;

@RestController
@RequestMapping("/results")
public class ResultController {

    private final ResultRepository results;
    private final ResultService stats;

    public ResultController(ResultRepository repo, ResultService svc) {
        this.results = repo;
        this.stats   = svc;
    }


    @GetMapping
    public List<Result> list(){ return results.findAll(); }

    @GetMapping("{id}")
    public Result get(@PathVariable Long id){
        return results.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"result "+id+" not found"));
    }

    @GetMapping("/student/{id}/average")
    public double meanForStudent(@PathVariable Long id) {
        return stats.avgForStudent(id);
    }

    // GET /results/student/{id}/subject/{sub}/average
    @GetMapping("/student/{id}/subject/{sub}/average")
    public double meanForStudentInSubject(@PathVariable Long id,
                                        @PathVariable("sub") Long subjectId) {
        return stats.avgForStudentSubject(id, subjectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result create(@RequestBody Result body){
        if(body.getScore()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"score required");
        if(body.getStudent()==null || body.getExam()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"student and exam required");
        return results.save(body);
    }

    @PutMapping("{id}")
    public Result update(@PathVariable Long id,@RequestBody Result in){
        Result r = get(id);
        if(in.getScore()!=null) r.setScore(in.getScore());
        return results.save(r);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ results.deleteById(id); }
}
