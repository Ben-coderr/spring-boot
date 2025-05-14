package com.school.parent.service;
import java.util.Set;
import java.util.stream.Collectors;
import com.school.student.model.Student;

import com.school.parent.dto.ParentDTO;
import com.school.parent.model.Parent;
import com.school.parent.repository.ParentRepository;
import com.school.student.repository.StudentRepository;
// import com.school.teacher.model.Teacher;
import com.school.common.enums.Role;
import com.school.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;




@Service
@RequiredArgsConstructor
@Transactional
public class ParentService {

    private final ParentRepository repo;
    private final StudentRepository studentRepo;
    private final PasswordEncoder encoder;
    @Transactional(readOnly = true)
    public List<ParentDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public ParentDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Parent " + id + " not found")));
    }

    public ParentDTO create(ParentDTO dto) {
    if (dto.getPassword() == null || dto.getPassword().isBlank())
        throw new IllegalArgumentException("password is required");
    Parent t = toEntity(dto);
    t.setPassword(encoder.encode(dto.getPassword()));
    t.setRole(Role.PARENT);
    return toDto(repo.save(t));
    }

    public ParentDTO update(Long id, ParentDTO dto) {
        Parent p = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Parent " + id + " not found"));
        p.setFullName(dto.getFullName());
        p.setEmail(dto.getEmail());
        return toDto(repo.save(p));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Parent " + id + " not found");
        repo.deleteById(id);
    }

    private ParentDTO toDto(Parent p) {
        return ParentDTO.builder()
                .id(p.getId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .childIds(
                    p.getChildren().stream()
                        .map(Student::getId)
                        .collect(Collectors.toSet())
                )
                .build();
    }

    private Parent toEntity(ParentDTO d) {
        return Parent.builder()
                .fullName(d.getFullName())
                .email(d.getEmail())
                .role(Role.PARENT)
                .children(resolveStudents(d.getChildIds()))
                .build();
    }


/* ---------- helpers ---------- */
private Set<Student> resolveStudents(Set<Long> ids) {
   if (ids == null || ids.isEmpty()) return new HashSet<>();
   return ids.stream()
             .map(id -> studentRepo.findById(id)
                     .orElseThrow(() -> new NotFoundException("Student "+id+" not found")))
             .collect(Collectors.toSet());
}
}
