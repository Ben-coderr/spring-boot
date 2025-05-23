package com.school.schoolclass.controller;
import com.school.common.exception.NotFoundException;
import com.school.schoolclass.dto.SchoolClassDTO;
import com.school.schoolclass.service.SchoolClassService;
import com.school.schoolclass.model.SchoolClass;
import com.school.schoolclass.repository.SchoolClassRepository;
import com.school.student.dto.StudentDTO;
import com.school.student.service.StudentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class SchoolClassController {

    private final SchoolClassService svc;
    private final SchoolClassRepository classRepo;   // NEW
    private final StudentService studentService;     // NEW

    @GetMapping
    public List<SchoolClassDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public SchoolClassDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<SchoolClassDTO> create(@RequestBody SchoolClassDTO dto) {
        SchoolClassDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/classes/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public SchoolClassDTO update(@PathVariable Long id, @RequestBody SchoolClassDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }

    /* ── placeholder sub-routes ───────────── */
    @GetMapping("/{id}/students")
    public List<StudentDTO> students(@PathVariable Long id) {
        SchoolClass sc = classRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Class " + id + " not found"));
        return sc.getStudents()
                 .stream()
                 .map(studentService::toDto)
                 .toList();
    }

    @GetMapping("/{id}/lessons")
    public List<?> lessons(@PathVariable Long id) { return List.of(); }

    @GetMapping("/{id}/events")
    public List<?> events(@PathVariable Long id) { return List.of(); }

    @GetMapping("/{id}/announcements")
    public List<?> ann(@PathVariable Long id) { return List.of(); }
}
