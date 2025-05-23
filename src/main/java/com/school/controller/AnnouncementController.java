package com.school.controller;

import com.school.model.Announcement;
import com.school.repository.AnnouncementRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementRepository announces;
    public AnnouncementController(AnnouncementRepository repo){ announces = repo; }

    @GetMapping
    public List<Announcement> list(){ return announces.findAll(); }

    @GetMapping("{id}")
    public Announcement get(@PathVariable Long id){
        return announces.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"announcement "+id+" not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Announcement create(@RequestBody Announcement body){
        if(body.getTitle()==null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.getContent()==null || body.getContent().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"content required");
        return announces.save(body);
    }

    @PutMapping("{id}")
    public Announcement update(@PathVariable Long id,@RequestBody Announcement in){
        Announcement a = get(id);
        if(in.getTitle()!=null)   a.setTitle(in.getTitle());
        if(in.getContent()!=null) a.setContent(in.getContent());
        return announces.save(a);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ announces.deleteById(id); }
}
