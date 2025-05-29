package com.school.controller;

import com.school.model.Subject;
import com.school.repository.SubjectRepository;
import com.school.dto.SubjectDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/subjects")
public class SubjectController { // handle subjects

    private final SubjectRepository subjectRepo;
    public SubjectController(SubjectRepository repo){ this.subjectRepo = repo; }

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
}

