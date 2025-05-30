package com.school.controller;

import com.school.model.*;
import com.school.repository.*;
import com.school.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.ClassManagementService;


import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private final GradeRepository gradeRepo;
    private final SchoolClassRepository classRepo;
    private final LessonRepository lessonRepo;
    private final ClassManagementService classManager;

    public GradeController(GradeRepository repo,
                           SchoolClassRepository classRepo,
                           LessonRepository lessonRepo,
                           ClassManagementService mgr){
        this.gradeRepo = repo;
        this.classRepo = classRepo;
        this.lessonRepo = lessonRepo;
        this.classManager = mgr;
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

    // subjects taught in this grade
    @GetMapping("{id}/subjects")
    public List<Long> gradeSubjects(@PathVariable Long id) {
        gradeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "grade " + id + " not found"));
        List<Lesson> lessons = lessonRepo.findBySchoolClass_Grade_Id(id);
        java.util.Set<Long> unique = new java.util.HashSet<>();
        for (Lesson l : lessons) {
            if (l.getSubject() != null) unique.add(l.getSubject().getId());
        }
        return new java.util.ArrayList<>(unique);
    }

    // bulk promotion for all classes in grade
    @PostMapping("{id}/promote")
    public void promoteGrade(@PathVariable Long id) {
        for (SchoolClass cl : classRepo.findByGrade_Id(id)) {
            classManager.promoteClass(cl.getId(), null);
        }
    }

    @DeleteMapping("{id}")
    public void removeGrade(@PathVariable Long id){ gradeRepo.deleteById(id); }
}
