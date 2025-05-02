package com.school.subject.controller;

import com.school.subject.dto.SubjectDTO;
import com.school.subject.service.SubjectService;
import com.school.teacher.dto.TeacherDTO;
import com.school.teacher.service.TeacherService;
import com.school.lesson.dto.LessonDTO;
import com.school.lesson.service.LessonService;
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
    private final TeacherService teacherSvc;   // ← ADD
    private final LessonService  lessonSvc;    // ← ADD

    /* ---------- CRUD ---------- */

    @GetMapping
    public List<SubjectDTO> getAll() {
        return svc.findAll();
    }

    @GetMapping("/{id}")
    public SubjectDTO getOne(@PathVariable Long id) {
        return svc.findById(id);
    }

    @PostMapping
    public ResponseEntity<SubjectDTO> create(@RequestBody SubjectDTO dto) {
        SubjectDTO saved = svc.create(dto);
        return ResponseEntity
                .created(URI.create("/api/subjects/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public SubjectDTO update(@PathVariable Long id,
                             @RequestBody SubjectDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        svc.delete(id);
    }

    /* ---------- sub-routes ---------- */

    /**
     * GET /api/subjects/{id}/teachers  
     * returns all teachers who teach the subject
     */
    @GetMapping("/{id}/teachers")
    public List<TeacherDTO> teachers(@PathVariable Long id) {
        return teacherSvc.findBySubject(id);       // implemented in TeacherService
    }

    /**
     * GET /api/subjects/{id}/lessons  
     * returns all lessons mapped to the subject
     */
    @GetMapping("/{id}/lessons")
    public List<LessonDTO> lessons(@PathVariable Long id) {
        return lessonSvc.findBySubject(id);        // implemented in LessonService
    }
}
