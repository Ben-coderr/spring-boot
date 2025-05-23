package com.school.controller;

import com.school.model.Assignment;
import com.school.repository.AssignmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentRepository assignments;
    public AssignmentController(AssignmentRepository repo){ assignments = repo; }

    @GetMapping
    public List<Assignment> list(){ return assignments.findAll(); }

    @GetMapping("{id}")
    public Assignment get(@PathVariable Long id){
        return assignments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"assignment "+id+" not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Assignment create(@RequestBody Assignment body){
        if(body.getTitle()==null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.getLesson()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"lesson required");
        return assignments.save(body);
    }

    @PutMapping("{id}")
    public Assignment update(@PathVariable Long id,@RequestBody Assignment in){
        Assignment a = get(id);
        if(in.getTitle()!=null)   a.setTitle(in.getTitle());
        if(in.getDueDate()!=null) a.setDueDate(in.getDueDate());
        return assignments.save(a);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ assignments.deleteById(id); }
}
