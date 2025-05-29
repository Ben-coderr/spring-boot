package com.school.controller;

import com.school.model.Assignment;
import com.school.repository.AssignmentRepository;
import com.school.dto.AssignmentDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentRepository assignmentRepo;
    public AssignmentController(AssignmentRepository repo){ this.assignmentRepo = repo; }

    @GetMapping
    public List<AssignmentDto> allAssignments(){
        List<Assignment> all = assignmentRepo.findAll();
        List<AssignmentDto> out = new ArrayList<>();
        for (Assignment assignment : all) {
            out.add(AssignmentDto.from(assignment));
        }
        return out;
    }

    @GetMapping("{id}")
    public AssignmentDto findAssignment(@PathVariable Long id){
        Assignment assignment = assignmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"assignment "+id+" not found"));
        return AssignmentDto.from(assignment);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentDto createAssignment(@RequestBody Assignment body){
        if(body.getTitle()==null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.getLesson()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"lesson required");
        return AssignmentDto.from(assignmentRepo.save(body));
    }

    @PutMapping("{id}")
    public AssignmentDto updateAssignment(@PathVariable Long id,@RequestBody Assignment in){
        Assignment assignment = assignmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"assignment "+id+" not found"));
        if(in.getTitle()!=null)   assignment.setTitle(in.getTitle());
        if(in.getDueDate()!=null) assignment.setDueDate(in.getDueDate());
        return AssignmentDto.from(assignmentRepo.save(assignment));
    }

    @DeleteMapping("{id}")
    public void removeAssignment(@PathVariable Long id){ assignmentRepo.deleteById(id); }
}
