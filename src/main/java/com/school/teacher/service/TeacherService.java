package com.school.teacher.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.school.common.exception.NotFoundException;
import com.school.subject.model.Subject;
import com.school.subject.repository.SubjectRepository;
import com.school.teacher.dto.TeacherDTO;
import com.school.teacher.model.Teacher;
import com.school.teacher.repository.TeacherRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository repo;
    private final SubjectRepository subjectRepo;

    public List<TeacherDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public TeacherDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Teacher " + id + " not found")));
    }

    public TeacherDTO create(TeacherDTO dto) {
        return toDto(repo.save(toEntity(dto)));
    }

    public TeacherDTO update(Long id, TeacherDTO dto) {
        Teacher t = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Teacher " + id + " not found"));
        t.setFullName(dto.getFullName());
        t.setEmail(dto.getEmail());
        return toDto(repo.save(t));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Teacher " + id + " not found");
        repo.deleteById(id);
    }
    public List<TeacherDTO> findBySubject(Long subjectId) {
        List<Teacher> teachers = repo.findAllBySubjects_Id(subjectId);
        if (teachers.isEmpty()) {
            throw new NotFoundException("No teachers found for subject " + subjectId);
        }
        return teachers.stream().map(this::toDto).toList();
    }

    @Transactional
    public void assignSubject(Long tid, Long sid) {
        Teacher  t = repo.findById(tid)
                         .orElseThrow(() -> new NotFoundException("Teacher " + tid));
        Subject  s = subjectRepo.findById(sid)
                         .orElseThrow(() -> new NotFoundException("Subject " + sid));
    
        t.getSubjects().add(s);
        s.getTeachers().add(t);     // keep both sides in sync
    }
    
    @Transactional
    public void unassignSubject(Long tid, Long sid) {
        Teacher  t = repo.findById(tid)
                         .orElseThrow(() -> new NotFoundException("Teacher " + tid));
        Subject  s = subjectRepo.findById(sid)
                         .orElseThrow(() -> new NotFoundException("Subject " + sid));
    
        t.getSubjects().remove(s);
        s.getTeachers().remove(t);
    }
    
    /* ---------- mapping helpers ---------- */
    private TeacherDTO toDto(Teacher t) {
        return TeacherDTO.builder()
                .id(t.getId())
                .fullName(t.getFullName())
                .email(t.getEmail())
                .subjectIds(
                    t.getSubjects()
                    .stream().map(Subject::getId)
                    .collect(Collectors.toSet())
                )

                .build();
    }

    private Teacher toEntity(TeacherDTO d) {
        return Teacher.builder()
                .fullName(d.getFullName())
                .email(d.getEmail())
                .subjects(resolveSubjects(d.getSubjectIds()))
                .build();
    }

    //Helpers 
    private Set<Subject> resolveSubjects(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return ids.stream()
                .map(id -> subjectRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Subject " + id)))
                .collect(Collectors.toSet());
    }

}
