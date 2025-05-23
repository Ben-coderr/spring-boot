package com.school.controller;

import com.school.model.Parent;
import com.school.repository.ParentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/parents")
public class ParentController {

    private final ParentRepository parents;
    public ParentController(ParentRepository repo){ this.parents = repo; }

    private static void must(String v,String f){
        if(v==null||v.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,f+" required");
    }

    @GetMapping
    public List<Parent> list(){ return parents.findAll(); }

    @GetMapping("{id}")
    public Parent get(@PathVariable Long id){
        return parents.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"parent "+id+" not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Parent add(@RequestBody Parent body){
        must(body.getFullName(),"name");
        must(body.getPassword(),"password");
        return parents.save(body);
    }

    @PutMapping("{id}")
    public Parent edit(@PathVariable Long id,@RequestBody Parent in){
        Parent p = get(id);
        if(in.getFullName()!=null) p.setFullName(in.getFullName());
        if(in.getEmail()!=null)    p.setEmail(in.getEmail());
        if(in.getPassword()!=null) p.setPassword(in.getPassword());
        return parents.save(p);
    }

    @DeleteMapping("{id}")
    public void remove(@PathVariable Long id){ parents.deleteById(id); }
}
