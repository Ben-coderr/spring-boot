package project.edusphere.assignment.service;

import project.edusphere.common.exception.NotFoundException;
import project.edusphere.assignment.dto.AssignmentDTO;
import project.edusphere.assignment.model.Assignment;
import project.edusphere.assignment.repository.AssignmentRepository;
import project.edusphere.lesson.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;




@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {

    private final AssignmentRepository repo;
    private final LessonRepository lessonRepo;
    @Transactional(readOnly = true)
    public List<AssignmentDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public AssignmentDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Assignment " + id + " not found")));
    }

    public AssignmentDTO create(AssignmentDTO dto) { return toDto(repo.save(toEntity(dto))); }

    public AssignmentDTO update(Long id, AssignmentDTO dto) {
        Assignment a = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Assignment " + id + " not found"));
        a.setTitle(dto.getTitle());
        a.setDueDate(dto.getDueDate());
        a.setLesson(lessonRepo.findById(dto.getLessonId())
                .orElseThrow(() -> new NotFoundException("Lesson " + dto.getLessonId() + " not found")));
        return toDto(repo.save(a));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Assignment " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- mapping helpers ---------- */
    private AssignmentDTO toDto(Assignment a) {
        return AssignmentDTO.builder()
                .id(a.getId())
                .title(a.getTitle())
                .dueDate(a.getDueDate())
                .lessonId(a.getLesson().getId())
                .build();
    }

    private Assignment toEntity(AssignmentDTO d) {
        return Assignment.builder()
                .title(d.getTitle())
                .dueDate(d.getDueDate())
                .lesson(lessonRepo.findById(d.getLessonId())
                        .orElseThrow(() -> new NotFoundException("Lesson " + d.getLessonId() + " not found")))
                .build();
    }
}
