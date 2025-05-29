package com.school.controller;

import com.school.model.*;
import com.school.repository.*;
import com.school.dto.*;
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

    private final SchoolClassRepository classRepo;
    private final ClassRankingService   rankingService;
    private final StudentRepository     studentRepo;

    public SchoolClassController(SchoolClassRepository repo,
                                 ClassRankingService   rnk,
                                 StudentRepository     studentRepo) {
        this.classRepo = repo;
        this.rankingService = rnk;
        this.studentRepo = studentRepo;
    }


    @GetMapping
    public List<SchoolClassDto> allClasses(){
        List<SchoolClass> all = classRepo.findAll();
        List<SchoolClassDto> out = new ArrayList<>();
        for (SchoolClass classEntity : all) {
            Long gradeId = (classEntity.getGrade() != null) ? classEntity.getGrade().getId() : null;
            out.add(new SchoolClassDto(classEntity.getId(), classEntity.getName(), gradeId));
        }
        return out;
    }

    @GetMapping("{id}")
    public SchoolClassDto findClass(@PathVariable Long id){
        SchoolClass classEntity = classRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"class "+id+" not found"));
        Long gradeId = (classEntity.getGrade()!=null) ? classEntity.getGrade().getId() : null;
        return new SchoolClassDto(classEntity.getId(), classEntity.getName(), gradeId);
    }

    @GetMapping("{id}/rank")
    public java.util.List<Map<String,Object>> rankingForClass(@PathVariable Long id) {
        findClass(id);
        return rankingService.ranking(id);
    }

    @GetMapping("{id}/students")
    public List<StudentDto> studentsInClass(@PathVariable Long id) {
        findClass(id);
        List<Student> all = studentRepo.findBySchoolClass_Id(id);
        List<StudentDto> out = new ArrayList<>();
        for (Student s : all) out.add(StudentMapper.toDto(s));
        return out;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SchoolClassDto createClass(@RequestBody SchoolClass body){
        if(body.getName()==null||body.getName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"name required");
        if(body.getGrade()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"grade required");
        SchoolClass saved = classRepo.save(body);
        Long gradeId = (saved.getGrade()!=null) ? saved.getGrade().getId() : null;
        return new SchoolClassDto(saved.getId(), saved.getName(), gradeId);
    }

    @PutMapping("{id}")
    public SchoolClassDto updateClass(@PathVariable Long id,@RequestBody SchoolClass in){
        SchoolClass classEntity = classRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"class "+id+" not found"));
        if(in.getName()!=null)     classEntity.setName(in.getName());
        if(in.getCapacity()!=null) classEntity.setCapacity(in.getCapacity());
        if(in.getGrade()!=null)    classEntity.setGrade(in.getGrade());
        if(in.getSupervisor()!=null)classEntity.setSupervisor(in.getSupervisor());
        SchoolClass saved = classRepo.save(classEntity);
        Long gradeId = (saved.getGrade()!=null) ? saved.getGrade().getId() : null;
        return new SchoolClassDto(saved.getId(), saved.getName(), gradeId);
    }

    @DeleteMapping("{id}")
    public void removeClass(@PathVariable Long id){ classRepo.deleteById(id); }
}
