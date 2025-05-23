package com.school.controller;

import com.school.model.Lesson;
import com.school.repository.LessonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/lessons")
public class LessonController {
    //TODO:add the validation method instead of old logic

    private final LessonRepository lessons;
    public LessonController(LessonRepository repo){ lessons = repo; }

    @GetMapping
    public List<Lesson> list(){ return lessons.findAll(); }

    @GetMapping("{id}")
    public Lesson get(@PathVariable Long id){
        return lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"lesson "+id+" not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lesson add(@RequestBody Lesson body){
        if(body.getTopic()==null || body.getTopic().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"topic required");
        if(body.getSubject()==null || body.getTeacher()==null || body.getSchoolClass()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"subject, teacher and class required");
        return lessons.save(body);
    }

    @PutMapping("{id}")
    public Lesson edit(@PathVariable Long id,@RequestBody Lesson in){
        Lesson l = get(id);
        if(in.getTopic()!=null)       l.setTopic(in.getTopic());
        if(in.getLessonDate()!=null)  l.setLessonDate(in.getLessonDate());
        if(in.getDay()!=null)         l.setDay(in.getDay());
        if(in.getStartTime()!=null)   l.setStartTime(in.getStartTime());
        if(in.getEndTime()!=null)     l.setEndTime(in.getEndTime());
        if(in.getSubject()!=null)     l.setSubject(in.getSubject());
        if(in.getTeacher()!=null)     l.setTeacher(in.getTeacher());
        if(in.getSchoolClass()!=null) l.setSchoolClass(in.getSchoolClass());
        return lessons.save(l);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ lessons.deleteById(id); }
}
