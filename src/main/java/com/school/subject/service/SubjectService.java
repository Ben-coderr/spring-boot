package com.school.subject.service;

import com.school.common.exception.NotFoundException;
import com.school.subject.dto.SubjectDTO;
import com.school.subject.model.Subject;
import com.school.subject.repository.SubjectRepository;
import com.school.teacher.model.Teacher;
import com.school.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;




@Service
@RequiredArgsConstructor
@Transactional          // ← every public method is now wrapped in a tx
public class SubjectService {

    private final SubjectRepository repo;
    private final TeacherRepository teacherRepo;        // ← ADD THIS

    /* ---------- CRUD ---------- */

    @Transactional(readOnly = true)
    public List<SubjectDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SubjectDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject " + id + " not found")));
    }

    public SubjectDTO create(SubjectDTO dto) {
        // optional uniqueness guard
        if (repo.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Subject name already exists");
        }
        return toDto(repo.save(toEntity(dto)));
    }

    public SubjectDTO update(Long id, SubjectDTO dto) {
        Subject s = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject " + id + " not found"));
        s.setName(dto.getName());
        return toDto(s);          // s is managed, save() not required
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Subject " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- RELATION HELPERS ---------- */

    /** Attach an existing subject to an existing teacher */
    public void assignToTeacher(Long subjectId, Long teacherId) {
        Subject  subject  = repo.findById(subjectId)
                                 .orElseThrow(() -> new NotFoundException("Subject " + subjectId));
        Teacher  teacher  = teacherRepo.findById(teacherId)
                                 .orElseThrow(() -> new NotFoundException("Teacher " + teacherId));

        subject.getTeachers().add(teacher);   // keep both sides in sync
        teacher.getSubjects().add(subject);
        // thanks to @Transactional the change flushes automatically
    }

    /** Remove the link between a subject and a teacher */
    public void unassignFromTeacher(Long subjectId, Long teacherId) {
        Subject subject = repo.findById(subjectId)
                              .orElseThrow(() -> new NotFoundException("Subject " + subjectId));
        Teacher teacher = teacherRepo.findById(teacherId)
                              .orElseThrow(() -> new NotFoundException("Teacher " + teacherId));

        subject.getTeachers().remove(teacher);
        teacher.getSubjects().remove(subject);
    }

    /* ---------- mapping helpers ---------- */

    private SubjectDTO toDto(Subject s) {
        return SubjectDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .build();
    }

    private Subject toEntity(SubjectDTO d) {
        return Subject.builder()
                .name(d.getName())
                .build();
    }
}
