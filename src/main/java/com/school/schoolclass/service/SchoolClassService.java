package com.school.schoolclass.service;

import com.school.common.exception.NotFoundException;
import com.school.grade.repository.GradeRepository;
import com.school.schoolclass.dto.SchoolClassDTO;
import com.school.schoolclass.model.SchoolClass;
import com.school.schoolclass.repository.SchoolClassRepository;
import com.school.teacher.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class SchoolClassService {

    private final SchoolClassRepository repo;
    private final GradeRepository gradeRepo;
    private final TeacherRepository teacherRepo;

    @Transactional(readOnly = true)
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
                c.setCapacity(dto.getCapacity());
                c.setSupervisor(dto.getSupervisorId() == null ? null :
                        teacherRepo.findById(dto.getSupervisorId())
                                   .orElseThrow(() -> new NotFoundException("Teacher "+dto.getSupervisorId()+" not found")));
        return toDto(repo.save(c));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Class " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- helpers ---------- */
    public  SchoolClassDTO toDto(SchoolClass c) {
        return SchoolClassDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .gradeId(c.getGrade().getId())
                .capacity(c.getCapacity())
                .supervisorId(c.getSupervisor() == null ? null : c.getSupervisor().getId())                
                .build();
    }

    public SchoolClass toEntity(SchoolClassDTO d) {
        return SchoolClass.builder()
                .name(d.getName())
                .grade(gradeRepo.findById(d.getGradeId())
                        .orElseThrow(() -> new NotFoundException("Grade " + d.getGradeId() + " not found")))
                .capacity(
                        d.getCapacity() != null ? d.getCapacity() : 30
                    )
                    .supervisor(
                        d.getSupervisorId() == null ? null
                                                    : teacherRepo.findById(d.getSupervisorId())
                                                                .orElseThrow(() -> new NotFoundException("Teacher "+d.getSupervisorId()+" not found"))
                    )
                .build();
    }
}
