package com.school.subject.service;

import com.school.common.exception.NotFoundException;
import com.school.subject.dto.SubjectDTO;
import com.school.subject.model.Subject;
import com.school.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repo;

    public List<SubjectDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public SubjectDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject " + id + " not found")));
    }

    public SubjectDTO create(SubjectDTO dto) {
        return toDto(repo.save(toEntity(dto)));
    }

    public SubjectDTO update(Long id, SubjectDTO dto) {
        Subject s = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject " + id + " not found"));
        s.setName(dto.getName());
        return toDto(repo.save(s));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Subject " + id + " not found");
        repo.deleteById(id);
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
