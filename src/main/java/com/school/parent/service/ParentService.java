package com.school.parent.service;

import com.school.parent.dto.ParentDTO;
import com.school.parent.model.Parent;
import com.school.parent.repository.ParentRepository;
import com.school.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository repo;

    public List<ParentDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public ParentDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Parent " + id + " not found")));
    }

    public ParentDTO create(ParentDTO dto) {
        return toDto(repo.save(toEntity(dto)));
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
                .build();
    }

    private Parent toEntity(ParentDTO d) {
        return Parent.builder()
                .fullName(d.getFullName())
                .email(d.getEmail())
                .build();
    }
}
