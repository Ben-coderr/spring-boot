package project.edusphere.lesson.controller;

import project.edusphere.lesson.dto.LessonDTO;
import project.edusphere.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService svc;

    @GetMapping
    public List<LessonDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public LessonDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<LessonDTO> create(@RequestBody LessonDTO dto) {
        LessonDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/lessons/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public LessonDTO update(@PathVariable Long id, @RequestBody LessonDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }

    /* ── placeholder sub-routes ─────────────────── */
    @GetMapping("/{id}/exams")
    public List<?> exams(@PathVariable Long id) { return List.of(); }

    @GetMapping("/{id}/assignments")
    public List<?> assignments(@PathVariable Long id) { return List.of(); }

    @GetMapping("/{id}/attendances")
    public List<?> attendances(@PathVariable Long id) { return List.of(); }
}
