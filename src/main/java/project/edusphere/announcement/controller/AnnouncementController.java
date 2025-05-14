package project.edusphere.announcement.controller;

import project.edusphere.announcement.dto.AnnouncementDTO;
import project.edusphere.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService svc;

    @GetMapping
    public List<AnnouncementDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public AnnouncementDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<AnnouncementDTO> create(@RequestBody AnnouncementDTO dto) {
        AnnouncementDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/announcements/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public AnnouncementDTO update(@PathVariable Long id, @RequestBody AnnouncementDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }
}
