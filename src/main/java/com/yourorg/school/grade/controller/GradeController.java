package com.yourorg.school.grade.controller;

import com.yourorg.school.grade.dto.GradeDTO;
import com.yourorg.school.grade.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService svc;

    @GetMapping
    public List<GradeDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public GradeDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<GradeDTO> create(@RequestBody GradeDTO dto) {
        GradeDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/grades/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public GradeDTO update(@PathVariable Long id, @RequestBody GradeDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }

    /* ── placeholder routes required by spec ───────── */
    @GetMapping("/{id}/students")
    public List<?> studentsInGrade(@PathVariable Long id) { return List.of(); }

    @GetMapping("/{id}/classes")
    public List<?> classesInGrade(@PathVariable Long id) { return List.of(); }
}
