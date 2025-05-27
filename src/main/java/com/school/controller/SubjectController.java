package com.school.controller;

import com.school.model.Subject;
import com.school.repository.SubjectRepository;
import com.school.dto.SubjectDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectRepository subjects;
    public SubjectController(SubjectRepository repo){ this.subjects = repo; }

    @GetMapping
    public List<SubjectDto> list(){
        return subjects.findAll().stream().map(SubjectDto::from).toList();
    }

    @GetMapping("{id}")
    public SubjectDto get(@PathVariable Long id){
        Subject s = subjects.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"subject "+id+" not found"));
        return SubjectDto.from(s);
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
        Subject s = subjects.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"subject "+id+" not found"));
        if(in.getName()!=null) s.setName(in.getName());
        return SubjectDto.from(subjects.save(s));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ subjects.deleteById(id); }
}
