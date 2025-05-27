package com.school.controller;

import com.school.model.Grade;
import com.school.repository.GradeRepository;
import com.school.dto.GradeDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private final GradeRepository grades;
    public GradeController(GradeRepository repo){ this.grades = repo; }


    @GetMapping
    public List<GradeDto> list(){
        return grades.findAll().stream().map(g -> new GradeDto(g.getId(), g.getLevel())).toList();
    }

    @GetMapping("{id}")
    public GradeDto get(@PathVariable Long id){
        Grade g = grades.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"grade "+id+" not found"));
        return new GradeDto(g.getId(), g.getLevel());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GradeDto add(@RequestBody Grade body){
        if(body.getLevel()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"level required");
        return new GradeDto(grades.save(body).getId(), body.getLevel());
    }

    @PutMapping("{id}")
    public GradeDto edit(@PathVariable Long id,@RequestBody Grade in){
        Grade g = grades.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"grade "+id+" not found"));
        if(in.getLevel()!=null) g.setLevel(in.getLevel());
        return new GradeDto(grades.save(g).getId(), g.getLevel());
    }

    @DeleteMapping("{id}")
    public void drop(@PathVariable Long id){ grades.deleteById(id); }
}
