package com.school.controller;

import com.school.model.Subject;
import com.school.repository.SubjectRepository;
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
    public List<Subject> list(){ return subjects.findAll(); }

    @GetMapping("{id}")
    public Subject get(@PathVariable Long id){
        return subjects.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"subject "+id+" not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Subject add(@RequestBody Subject body){
        if(body.getName()==null || body.getName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"name required");
        return subjects.save(body);
    }

    @PutMapping("{id}")
    public Subject edit(@PathVariable Long id,@RequestBody Subject in){
        Subject s = get(id);
        if(in.getName()!=null) s.setName(in.getName());
        return subjects.save(s);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ subjects.deleteById(id); }
}
