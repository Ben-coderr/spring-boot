package com.school.controller;

import com.school.model.*;
import com.school.repository.*;
import com.school.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private final GradeRepository gradeRepo;
    private final SchoolClassRepository classRepo;
    public GradeController(GradeRepository repo, SchoolClassRepository classRepo){
        this.gradeRepo = repo;
        this.classRepo = classRepo;
    }


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

    @GetMapping("{id}/classes")
    public List<SchoolClassDto> classesForGrade(@PathVariable Long id) {
        gradeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "grade " + id + " not found"));
        List<SchoolClass> all = classRepo.findByGrade_Id(id);
        List<SchoolClassDto> out = new ArrayList<>();
        for (SchoolClass schoolClass : all) {
            Long gid = (schoolClass.getGrade() != null) ? schoolClass.getGrade().getId() : null;
            out.add(new SchoolClassDto(schoolClass.getId(), schoolClass.getName(), gid));
        }
        return out;
    }

    @DeleteMapping("{id}")
    public void removeGrade(@PathVariable Long id){ gradeRepo.deleteById(id); }
}
