package com.yourorg.school.student.service;

import com.yourorg.school.student.dto.StudentDTO;
import com.yourorg.school.student.model.Student;
import com.yourorg.school.student.repository.StudentRepository;
import com.yourorg.school.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repo;

    public List<StudentDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public StudentDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Student " + id + " not found")));
    }

    public StudentDTO create(StudentDTO dto) {
        return toDto(repo.save(toEntity(dto)));
    }

    public StudentDTO update(Long id, StudentDTO dto) {
        Student st = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Student " + id + " not found"));
        st.setFullName(dto.getFullName());
        st.setEmail(dto.getEmail());
        return toDto(repo.save(st));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Student " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- mapping helpers ---------- */
    private StudentDTO toDto(Student s) {
        return StudentDTO.builder()
                .id(s.getId())
                .fullName(s.getFullName())
                .email(s.getEmail())
                .build();
    }

    private Student toEntity(StudentDTO d) {
        return Student.builder()
                .fullName(d.getFullName())
                .email(d.getEmail())
                .build();
    }
}
