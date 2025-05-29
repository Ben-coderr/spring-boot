package com.school.controller;

import com.school.model.Lesson;
import com.school.repository.LessonRepository;
import com.school.dto.LessonDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/lessons")
public class LessonController { // lessons api
    //TODO:add the validation method instead of old logic

    private final LessonRepository lessons;
    public LessonController(LessonRepository repo){ lessons = repo; }

    @GetMapping
    public List<LessonDto> list(){
        List<Lesson> all = lessons.findAll();
        List<LessonDto> out = new ArrayList<>();
        for (Lesson lesson : all) {
            out.add(LessonDto.from(lesson));
        }
        return out;
    }

    @GetMapping("{id}")
    public LessonDto get(@PathVariable Long id){
        Lesson lesson = lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"lesson "+id+" not found"));
        return LessonDto.from(lesson);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonDto add(@RequestBody Lesson body){
        if(body.getTopic()==null || body.getTopic().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"topic required");
        if(body.getSubject()==null || body.getTeacher()==null || body.getSchoolClass()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"subject, teacher and class required");
        return LessonDto.from(lessons.save(body));
    }

    @PutMapping("{id}")
    public LessonDto edit(@PathVariable Long id,@RequestBody Lesson in){
        Lesson lesson = lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"lesson "+id+" not found"));
        if(in.getTopic()!=null)       lesson.setTopic(in.getTopic());
        if(in.getLessonDate()!=null)  lesson.setLessonDate(in.getLessonDate());
        if(in.getDay()!=null)         lesson.setDay(in.getDay());
        if(in.getStartTime()!=null)   lesson.setStartTime(in.getStartTime());
        if(in.getEndTime()!=null)     lesson.setEndTime(in.getEndTime());
        if(in.getSubject()!=null)     lesson.setSubject(in.getSubject());
        if(in.getTeacher()!=null)     lesson.setTeacher(in.getTeacher());
        if(in.getSchoolClass()!=null) lesson.setSchoolClass(in.getSchoolClass());
        return LessonDto.from(lessons.save(lesson));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ lessons.deleteById(id); }
}
