package project.edusphere.schoolclass.controller;
import project.edusphere.common.exception.NotFoundException;
import project.edusphere.schoolclass.dto.SchoolClassDTO;
import project.edusphere.schoolclass.service.SchoolClassService;
import project.edusphere.schoolclass.model.SchoolClass;
import project.edusphere.schoolclass.repository.SchoolClassRepository;
import project.edusphere.student.dto.StudentDTO;
import project.edusphere.student.service.StudentService;

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
