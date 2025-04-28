package com.school.teacher.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.school.teacher.dto.TeacherDTO;
import com.school.teacher.service.TeacherService;

import lombok.RequiredArgsConstructor;

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

    /* ───── NEW incremental routes ───────────────────────── */

    /** Assign this teacher to an existing subject */
    @PostMapping("/{tid}/subjects/{sid}")
    public void assign(@PathVariable Long tid, @PathVariable Long sid) {
        svc.assignSubject(tid, sid);
    }

    /** Remove the link again */
    @DeleteMapping("/{tid}/subjects/{sid}")
    public void unassign(@PathVariable Long tid, @PathVariable Long sid) {
        svc.unassignSubject(tid, sid);
    }
}
