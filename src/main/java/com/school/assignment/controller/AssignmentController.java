package com.school.assignment.controller;

import com.school.assignment.dto.AssignmentDTO;
import com.school.assignment.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService svc;

    @GetMapping
    public List<AssignmentDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public AssignmentDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<AssignmentDTO> create(@RequestBody AssignmentDTO dto) {
        AssignmentDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/assignments/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public AssignmentDTO update(@PathVariable Long id, @RequestBody AssignmentDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }

    /* ── placeholder sub-route ───────────── */
    @GetMapping("/{id}/results")
    public List<?> results(@PathVariable Long id) { return List.of(); }
}
