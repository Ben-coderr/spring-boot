package com.school.subject.controller;

import com.school.subject.dto.SubjectDTO;
import com.school.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService svc;

    @GetMapping
    public List<SubjectDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public SubjectDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<SubjectDTO> create(@RequestBody SubjectDTO dto) {
        SubjectDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/subjects/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public SubjectDTO update(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }

    /* ── placeholder sub-routes ─────────── */
    @GetMapping("/{id}/teachers")
    public List<?> teachers(@PathVariable Long id) { return List.of(); }

    @GetMapping("/{id}/lessons")
    public List<?> lessons(@PathVariable Long id) { return List.of(); }
}
