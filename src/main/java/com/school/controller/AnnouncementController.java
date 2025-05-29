package com.school.controller;

import com.school.model.Announcement;
import com.school.repository.AnnouncementRepository;
import com.school.dto.AnnouncementDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementRepository announces; // repository for messages
    public AnnouncementController(AnnouncementRepository repo){ announces = repo; }

    @GetMapping
    public List<AnnouncementDto> list(){ // get all announcements
        List<Announcement> all = announces.findAll();
        List<AnnouncementDto> out = new ArrayList<>();
        for (Announcement announcement : all) {
            out.add(AnnouncementDto.from(announcement));
        }
        return out;
    }

    @GetMapping("{id}")
    public AnnouncementDto get(@PathVariable Long id){ // get one by id
        Announcement announcement = announces.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"announcement "+id+" not found"));
        return AnnouncementDto.from(announcement);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnnouncementDto create(@RequestBody Announcement body){ // save new
        if(body.getTitle()==null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.getContent()==null || body.getContent().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"content required");
        return AnnouncementDto.from(announces.save(body));
    }

    @PutMapping("{id}")
    public AnnouncementDto update(@PathVariable Long id,@RequestBody Announcement in){ // update some fields
        Announcement announcement = announces.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"announcement "+id+" not found"));
        if(in.getTitle()!=null)   announcement.setTitle(in.getTitle());
        if(in.getContent()!=null) announcement.setContent(in.getContent());
        return AnnouncementDto.from(announces.save(announcement));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ announces.deleteById(id); } // remove record
}
