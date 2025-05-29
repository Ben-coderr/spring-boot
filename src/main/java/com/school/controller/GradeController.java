package com.school.controller;

import com.school.model.Grade;
import com.school.repository.GradeRepository;
import com.school.dto.GradeDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private final GradeRepository gradeRepo;
    public GradeController(GradeRepository repo){ this.gradeRepo = repo; }


    @GetMapping
    public List<GradeDto> allGrades(){
        List<Grade> all = gradeRepo.findAll();
        List<GradeDto> out = new ArrayList<>();
        for (Grade grade : all) {
            out.add(new GradeDto(grade.getId(), grade.getLevel()));
        }
        return out;
    }

    @GetMapping("{id}")
    public GradeDto findGrade(@PathVariable Long id){
        Grade grade = gradeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"grade "+id+" not found"));
        return new GradeDto(grade.getId(), grade.getLevel());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GradeDto createGrade(@RequestBody Grade body){
        if(body.getLevel()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"level required");
        return new GradeDto(gradeRepo.save(body).getId(), body.getLevel());
    }

    @PutMapping("{id}")
    public GradeDto updateGrade(@PathVariable Long id,@RequestBody Grade in){
        Grade grade = gradeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"grade "+id+" not found"));
        if(in.getLevel()!=null) grade.setLevel(in.getLevel());
        return new GradeDto(gradeRepo.save(grade).getId(), grade.getLevel());
    }

    @DeleteMapping("{id}")
    public void removeGrade(@PathVariable Long id){ gradeRepo.deleteById(id); }
}
