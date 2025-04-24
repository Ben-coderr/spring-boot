package com.yourorg.school.schoolclass.service;

import com.yourorg.school.common.exception.NotFoundException;
import com.yourorg.school.grade.repository.GradeRepository;
import com.yourorg.school.schoolclass.dto.SchoolClassDTO;
import com.yourorg.school.schoolclass.model.SchoolClass;
import com.yourorg.school.schoolclass.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

    private final SchoolClassRepository repo;
    private final GradeRepository gradeRepo;

    public List<SchoolClassDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public SchoolClassDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Class " + id + " not found")));
    }

    public SchoolClassDTO create(SchoolClassDTO dto) {
        return toDto(repo.save(toEntity(dto)));
    }

    public SchoolClassDTO update(Long id, SchoolClassDTO dto) {
        SchoolClass c = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Class " + id + " not found"));
        c.setName(dto.getName());
        c.setGrade(gradeRepo.findById(dto.getGradeId())
                .orElseThrow(() -> new NotFoundException("Grade " + dto.getGradeId() + " not found")));
        return toDto(repo.save(c));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Class " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- helpers ---------- */
    private SchoolClassDTO toDto(SchoolClass c) {
        return SchoolClassDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .gradeId(c.getGrade().getId())
                .build();
    }

    private SchoolClass toEntity(SchoolClassDTO d) {
        return SchoolClass.builder()
                .name(d.getName())
                .grade(gradeRepo.findById(d.getGradeId())
                        .orElseThrow(() -> new NotFoundException("Grade " + d.getGradeId() + " not found")))
                .build();
    }
}
