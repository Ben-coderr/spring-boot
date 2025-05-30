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
@RequestMapping("/subjects")
public class SubjectController { // handle subjects

    private final SubjectRepository subjectRepo;
    private final TeacherRepository teacherRepo;
    private final LessonRepository lessonRepo;
    public SubjectController(SubjectRepository repo,
                            TeacherRepository teacherRepo,
                            LessonRepository lessonRepo){
        this.subjectRepo = repo;
        this.teacherRepo = teacherRepo;
        this.lessonRepo  = lessonRepo;
    }

    @GetMapping
    public List<SubjectDto> allSubjects(){
        List<Subject> all = subjectRepo.findAll();
        List<SubjectDto> out = new ArrayList<>();
        for (Subject subject : all) {
            out.add(SubjectDto.from(subject));
        }
        return out;
    }

    @GetMapping("{id}")
    public SubjectDto findSubject(@PathVariable Long id){
        Subject subject = subjectRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"subject "+id+" not found"));
        return SubjectDto.from(subject);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectDto createSubject(@RequestBody Subject body){
        if(body.getName()==null || body.getName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"name required");
        return SubjectDto.from(subjectRepo.save(body));
    }

    @PutMapping("{id}")
    public SubjectDto updateSubject(@PathVariable Long id,@RequestBody Subject in){
        Subject subject = subjectRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"subject "+id+" not found"));
        if(in.getName()!=null) subject.setName(in.getName());
        if(in.getCoefficient()!=null) subject.setCoefficient(in.getCoefficient());
        if(in.getCcWeight()!=null) subject.setCcWeight(in.getCcWeight());
        if(in.getExamWeight()!=null) subject.setExamWeight(in.getExamWeight());
        if(in.getAttendanceWeight()!=null) subject.setAttendanceWeight(in.getAttendanceWeight());
        return SubjectDto.from(subjectRepo.save(subject));
    }

    @DeleteMapping("{id}")
    public void removeSubject(@PathVariable Long id){ subjectRepo.deleteById(id); }

    @GetMapping("{id}/teachers")
    public List<TeacherDto> teachersForSubject(@PathVariable Long id) {
        subjectRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "subject " + id + " not found"));
        List<Teacher> all = teacherRepo.findBySubject_Id(id);
        List<TeacherDto> out = new ArrayList<>();
        for (Teacher teacher : all) {
            out.add(TeacherDto.from(teacher));
        }
        return out;
    }

    @GetMapping("{id}/lessons")
    public List<LessonDto> lessonsForSubject(@PathVariable Long id) {
        subjectRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "subject " + id + " not found"));
        List<Lesson> all = lessonRepo.findBySubject_Id(id);
        List<LessonDto> out = new ArrayList<>();
        for (Lesson lesson : all) {
            out.add(LessonDto.from(lesson));
        }
        return out;
    }
}

