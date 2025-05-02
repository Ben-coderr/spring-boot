package com.school.exam.controller;

import com.school.exam.dto.ExamDTO;
import com.school.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService svc;

    @GetMapping
    public List<ExamDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public ExamDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<ExamDTO> create(@RequestBody ExamDTO dto) {
        ExamDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/exams/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ExamDTO update(@PathVariable Long id, @RequestBody ExamDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }

    /* ── placeholder sub-route ─────────── */
    @GetMapping("/{id}/results")
    public List<?> results(@PathVariable Long id) { return List.of(); }
}
