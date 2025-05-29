package com.school.controller;

import com.school.model.SchoolClass;
import com.school.repository.SchoolClassRepository;
import com.school.dto.SchoolClassDto;
import com.school.dto.GradeDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.ClassRankingService;     
import java.util.Map;

import java.util.List;
import java.util.ArrayList;

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
    public List<SchoolClassDto> list(){
        List<SchoolClass> all = classes.findAll();
        List<SchoolClassDto> out = new ArrayList<>();
        for (SchoolClass classEntity : all) {
            GradeDto gd = (classEntity.getGrade()!=null)
                    ? new GradeDto(classEntity.getGrade().getId(), classEntity.getGrade().getLevel())
                    : null;
            out.add(new SchoolClassDto(classEntity.getId(), classEntity.getName(), gd));
        }
        return out;
    }

    @GetMapping("{id}")
    public SchoolClassDto get(@PathVariable Long id){
        SchoolClass classEntity = classes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"class "+id+" not found"));
        GradeDto gd = (classEntity.getGrade()!=null)? new GradeDto(classEntity.getGrade().getId(), classEntity.getGrade().getLevel()) : null;
        return new SchoolClassDto(classEntity.getId(), classEntity.getName(), gd);
    }

    @GetMapping("{id}/rank")
    public java.util.List<Map<String,Object>> rank(@PathVariable Long id) {
        get(id);                     
        return ranking.ranking(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SchoolClassDto create(@RequestBody SchoolClass body){
        if(body.getName()==null||body.getName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"name required");
        if(body.getGrade()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"grade required");
        SchoolClass saved = classes.save(body);
        GradeDto gd = (saved.getGrade()!=null)? new GradeDto(saved.getGrade().getId(), saved.getGrade().getLevel()) : null;
        return new SchoolClassDto(saved.getId(), saved.getName(), gd);
    }

    @PutMapping("{id}")
    public SchoolClassDto update(@PathVariable Long id,@RequestBody SchoolClass in){
        SchoolClass classEntity = classes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"class "+id+" not found"));
        if(in.getName()!=null)     classEntity.setName(in.getName());
        if(in.getCapacity()!=null) classEntity.setCapacity(in.getCapacity());
        if(in.getGrade()!=null)    classEntity.setGrade(in.getGrade());
        if(in.getSupervisor()!=null)classEntity.setSupervisor(in.getSupervisor());
        SchoolClass saved = classes.save(classEntity);
        GradeDto gd = (saved.getGrade()!=null)? new GradeDto(saved.getGrade().getId(), saved.getGrade().getLevel()) : null;
        return new SchoolClassDto(saved.getId(), saved.getName(), gd);
    }

    @DeleteMapping("{id}")
    public void remove(@PathVariable Long id){ classes.deleteById(id); }
}
