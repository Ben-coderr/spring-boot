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

    private final SubjectRepository subjects;
    public SubjectController(SubjectRepository repo){ this.subjects = repo; }

    @GetMapping
    public List<SubjectDto> list(){
        List<Subject> all = subjects.findAll();
        List<SubjectDto> out = new ArrayList<>();
        for (Subject subject : all) {
            out.add(SubjectDto.from(subject));
        }
        return out;
    }

    @GetMapping("{id}")
    public SubjectDto get(@PathVariable Long id){
        Subject subject = subjects.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"subject "+id+" not found"));
        return SubjectDto.from(subject);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectDto add(@RequestBody Subject body){
        if(body.getName()==null || body.getName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"name required");
        return SubjectDto.from(subjects.save(body));
    }

    @PutMapping("{id}")
    public SubjectDto edit(@PathVariable Long id,@RequestBody Subject in){
        Subject subject = subjects.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"subject "+id+" not found"));
        if(in.getName()!=null) subject.setName(in.getName());
        if(in.getCoefficient()!=null) subject.setCoefficient(in.getCoefficient());
        if(in.getCcWeight()!=null) subject.setCcWeight(in.getCcWeight());
        if(in.getExamWeight()!=null) subject.setExamWeight(in.getExamWeight());
        if(in.getAttendanceWeight()!=null) subject.setAttendanceWeight(in.getAttendanceWeight());
        return SubjectDto.from(subjects.save(subject));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ subjects.deleteById(id); }
}

