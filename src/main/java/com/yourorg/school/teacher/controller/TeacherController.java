package com.yourorg.school.teacher.controller;

import com.yourorg.school.teacher.dto.TeacherDTO;
import com.yourorg.school.teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService svc;

    @GetMapping
    public List<TeacherDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public TeacherDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<TeacherDTO> create(@RequestBody TeacherDTO dto) {
        TeacherDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/teachers/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public TeacherDTO update(@PathVariable Long id, @RequestBody TeacherDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }
}
