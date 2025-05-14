package project.edusphere.teacher.controller;

// import project.edusphere.security.roles.IsAdmin;
import project.edusphere.security.roles.IsAdminOrTeacher;
// import project.edusphere.security.roles.IsTeacher;
import project.edusphere.teacher.dto.TeacherDTO;
import project.edusphere.teacher.service.TeacherService;
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
    @IsAdminOrTeacher    
    @GetMapping
    public List<TeacherDTO> getAll() { return svc.findAll(); }

    @IsAdminOrTeacher    
    @GetMapping("/{id}")
    public TeacherDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @IsAdminOrTeacher    
    @PostMapping
    public ResponseEntity<TeacherDTO> create(@RequestBody TeacherDTO dto) {
        TeacherDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/teachers/" + saved.getId())).body(saved);
    }

    @IsAdminOrTeacher    
    @PutMapping("/{id}")
    public TeacherDTO update(@PathVariable Long id, @RequestBody TeacherDTO dto) {
        return svc.update(id, dto);
    }

    @IsAdminOrTeacher    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }
}
