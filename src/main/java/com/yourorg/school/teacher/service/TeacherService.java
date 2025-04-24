package com.yourorg.school.teacher.service;

import com.yourorg.school.teacher.dto.TeacherDTO;
import com.yourorg.school.teacher.model.Teacher;
import com.yourorg.school.teacher.repository.TeacherRepository;
import com.yourorg.school.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository repo;

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

    /* ---------- mapping helpers ---------- */
    private TeacherDTO toDto(Teacher t) {
        return TeacherDTO.builder()
                .id(t.getId())
                .fullName(t.getFullName())
                .email(t.getEmail())
                .build();
    }

    private Teacher toEntity(TeacherDTO d) {
        return Teacher.builder()
                .fullName(d.getFullName())
                .email(d.getEmail())
                .build();
    }
}
