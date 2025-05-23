package com.school.controller;

import com.school.model.SchoolClass;
import com.school.repository.SchoolClassRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.ClassRankingService;     
import java.util.Map;       

import java.util.List;

@RestController
@RequestMapping("/classes")
public class SchoolClassController {

    private final SchoolClassRepository classes;
    private final ClassRankingService   ranking;

    public SchoolClassController(SchoolClassRepository repo,
                                 ClassRankingService   rnk) { // ← changed
        this.classes = repo;
        this.ranking = rnk;
    }


    @GetMapping
    public List<SchoolClass> list(){ return classes.findAll(); }

    @GetMapping("{id}")
    public SchoolClass get(@PathVariable Long id){
        return classes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"class "+id+" not found"));
    }

    @GetMapping("{id}/rank")
    public java.util.List<Map<String,Object>> rank(@PathVariable Long id) {
        get(id);                     
        return ranking.ranking(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SchoolClass create(@RequestBody SchoolClass body){
        if(body.getName()==null||body.getName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"name required");
        if(body.getGrade()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"grade required");
        return classes.save(body);
    }

    @PutMapping("{id}")
    public SchoolClass update(@PathVariable Long id,@RequestBody SchoolClass in){
        SchoolClass c = get(id);
        if(in.getName()!=null)     c.setName(in.getName());
        if(in.getCapacity()!=null) c.setCapacity(in.getCapacity());
        if(in.getGrade()!=null)    c.setGrade(in.getGrade());
        if(in.getSupervisor()!=null)c.setSupervisor(in.getSupervisor());
        return classes.save(c);
    }

    @DeleteMapping("{id}")
    public void remove(@PathVariable Long id){ classes.deleteById(id); }
}
