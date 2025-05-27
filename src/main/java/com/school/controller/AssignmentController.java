package com.school.controller;

import com.school.model.Assignment;
import com.school.repository.AssignmentRepository;
import com.school.dto.AssignmentDto;
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
    public List<AssignmentDto> list(){
        return assignments.findAll().stream().map(AssignmentDto::from).toList();
    }

    @GetMapping("{id}")
    public AssignmentDto get(@PathVariable Long id){
        Assignment a = assignments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"assignment "+id+" not found"));
        return AssignmentDto.from(a);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentDto create(@RequestBody Assignment body){
        if(body.getTitle()==null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.getLesson()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"lesson required");
        return AssignmentDto.from(assignments.save(body));
    }

    @PutMapping("{id}")
    public AssignmentDto update(@PathVariable Long id,@RequestBody Assignment in){
        Assignment a = assignments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"assignment "+id+" not found"));
        if(in.getTitle()!=null)   a.setTitle(in.getTitle());
        if(in.getDueDate()!=null) a.setDueDate(in.getDueDate());
        return AssignmentDto.from(assignments.save(a));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ assignments.deleteById(id); }
}
