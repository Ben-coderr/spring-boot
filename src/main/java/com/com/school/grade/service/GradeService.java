package com.com.school.grade.service;

import com.com.school.grade.dto.GradeDTO;
import com.com.school.grade.model.Grade;
import com.com.school.grade.repository.GradeRepository;
import com.com.school.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository repo;

    public List<GradeDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public GradeDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Grade " + id + " not found")));
    }

    public GradeDTO create(GradeDTO dto) {
        return toDto(repo.save(toEntity(dto)));
    }

    public GradeDTO update(Long id, GradeDTO dto) {
        Grade g = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Grade " + id + " not found"));
        g.setName(dto.getName());
        return toDto(repo.save(g));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Grade " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- mapping helpers ---------- */
    private GradeDTO toDto(Grade g) {
        return GradeDTO.builder()
                .id(g.getId())
                .name(g.getName())
                .build();
    }

    private Grade toEntity(GradeDTO d) {
        return Grade.builder()
                .name(d.getName())
                .build();
    }
}
