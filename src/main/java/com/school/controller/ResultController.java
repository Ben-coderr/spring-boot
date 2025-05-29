package com.school.controller;

import com.school.model.Result;
import com.school.repository.ResultRepository;
import com.school.dto.ResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.ResultService;

import java.util.List;
import java.util.ArrayList;

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
    public List<ResultDto> list(){
        List<Result> all = results.findAll();
        List<ResultDto> out = new ArrayList<>();
        for (Result result : all) {
            out.add(ResultDto.from(result));
        }
        return out;
    }

    @GetMapping("{id}")
    public ResultDto get(@PathVariable Long id){
        Result result = results.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"result "+id+" not found"));
        return ResultDto.from(result);
    }

    @GetMapping("/student/{id}/average")
    public double meanForStudent(@PathVariable Long id,
                                 @RequestParam(value = "scheme", required = false)
                                 Long schemeId) {
        return stats.avgForStudent(id, schemeId);
    }

    // GET /results/student/{id}/subject/{sub}/average
    @GetMapping("/student/{id}/subject/{sub}/average")
    public double meanForStudentInSubject(@PathVariable Long id,
                                         @PathVariable("sub") Long subjectId,
                                          @RequestParam(value = "scheme", required = false)
                                          Long schemeId) {
        return stats.avgForStudentSubject(id, subjectId, schemeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result create(@RequestBody Result body){
        if(body.getScore() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"score required");
        if(body.getStudent() == null || body.getExam() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"student and exam required");

        // If not passed in request, default isFinal to false
        if(body.getIsFinal() == null) body.setIsFinal(false);

        return results.save(body);
    }

    @PutMapping("{id}")
    public ResultDto update(@PathVariable Long id,@RequestBody Result in){
        Result result = results.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"result "+id+" not found"));
        if(in.getScore()!=null) result.setScore(in.getScore());
        return ResultDto.from(results.save(result));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ results.deleteById(id); }
}
