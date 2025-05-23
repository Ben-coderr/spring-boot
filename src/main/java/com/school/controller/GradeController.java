package com.school.controller;

import com.school.model.Grade;
import com.school.repository.GradeRepository;
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
    public List<Grade> list(){ return grades.findAll(); }

    @GetMapping("{id}")
    public Grade get(@PathVariable Long id){
        return grades.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"grade "+id+" not found"));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Grade add(@RequestBody Grade body){
        if(body.getLevel()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"level required");
        return grades.save(body);
    }

    @PutMapping("{id}")
    public Grade edit(@PathVariable Long id,@RequestBody Grade in){
        Grade g = get(id);
        if(in.getLevel()!=null) g.setLevel(in.getLevel());
        return grades.save(g);
    }

    @DeleteMapping("{id}")
    public void drop(@PathVariable Long id){ grades.deleteById(id); }
}
