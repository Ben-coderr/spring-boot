package com.school.controller;

import com.school.model.Assignment;
import com.school.repository.AssignmentRepository;
import com.school.repository.LessonRepository;
import com.school.dto.AssignmentDto;
import com.school.dto.AssignmentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentRepository assignmentRepo;
    private final LessonRepository     lessonRepo;
    public AssignmentController(AssignmentRepository repo,
                                LessonRepository lessons){
        this.assignmentRepo = repo;
        this.lessonRepo     = lessons;
    }

    @GetMapping
    public List<AssignmentDto> allAssignments(){
        List<Assignment> all = assignmentRepo.findAll();
        List<AssignmentDto> out = new ArrayList<>();
        for (Assignment assignment : all) {
            out.add(AssignmentMapper.toDto(assignment));
        }
        return out;
    }

    @GetMapping("{id}")
    public AssignmentDto findAssignment(@PathVariable Long id){
        Assignment assignment = assignmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"assignment "+id+" not found"));
        return AssignmentMapper.toDto(assignment);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentDto createAssignment(@RequestBody AssignmentDto body){
        if(body.title()==null || body.title().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.lessonId()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"lesson required");

        Assignment entity = AssignmentMapper.toEntity(body, lessonRepo);
        return AssignmentMapper.toDto(assignmentRepo.save(entity));
    }

    @PutMapping("{id}")
    public AssignmentDto updateAssignment(@PathVariable Long id,@RequestBody AssignmentDto in){
        Assignment assignment = assignmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"assignment "+id+" not found"));

        AssignmentMapper.copyOnWrite(in, assignment, lessonRepo);
        return AssignmentMapper.toDto(assignmentRepo.save(assignment));
    }

    @DeleteMapping("{id}")
    public void removeAssignment(@PathVariable Long id){ assignmentRepo.deleteById(id); }
}
