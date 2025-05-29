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

    private final AnnouncementRepository announcementRepo;
    public AnnouncementController(AnnouncementRepository repo){ this.announcementRepo = repo; }

    @GetMapping
    public List<AnnouncementDto> allAnnouncements(){
        List<Announcement> all = announcementRepo.findAll();
        List<AnnouncementDto> out = new ArrayList<>();
        for (Announcement announcement : all) {
            out.add(AnnouncementDto.from(announcement));
        }
        return out;
    }

    @GetMapping("{id}")
    public AnnouncementDto findAnnouncement(@PathVariable Long id){
        Announcement announcement = announcementRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"announcement "+id+" not found"));
        return AnnouncementDto.from(announcement);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnnouncementDto createAnnouncement(@RequestBody Announcement body){
        if(body.getTitle()==null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"title required");
        if(body.getContent()==null || body.getContent().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"content required");
        return AnnouncementDto.from(announcementRepo.save(body));
    }

    @PutMapping("{id}")
    public AnnouncementDto updateAnnouncement(@PathVariable Long id,@RequestBody Announcement in){
        Announcement announcement = announcementRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"announcement "+id+" not found"));
        if(in.getTitle()!=null)   announcement.setTitle(in.getTitle());
        if(in.getContent()!=null) announcement.setContent(in.getContent());
        return AnnouncementDto.from(announcementRepo.save(announcement));
    }

    @DeleteMapping("{id}")
    public void removeAnnouncement(@PathVariable Long id){ announcementRepo.deleteById(id); }
}
