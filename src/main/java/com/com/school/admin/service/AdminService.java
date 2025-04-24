package com.com.school.admin.service;

import com.com.school.admin.dto.AdminDTO;
import com.com.school.admin.model.Admin;
import com.com.school.admin.repository.AdminRepository;
import com.com.school.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository repo;

    public List<AdminDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public AdminDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin " + id + " not found")));
    }

    public AdminDTO create(AdminDTO dto) {
        Admin saved = repo.save(toEntity(dto));
        return toDto(saved);
    }

    public AdminDTO update(Long id, AdminDTO dto) {
        Admin admin = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin " + id + " not found"));

        admin.setFullName(dto.getFullName());
        admin.setEmail(dto.getEmail());
        return toDto(repo.save(admin));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Admin " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- mapping helpers ---------- */
    private AdminDTO toDto(Admin a) {
        return AdminDTO.builder()
                .id(a.getId())
                .fullName(a.getFullName())
                .email(a.getEmail())
                .build();
    }

    private Admin toEntity(AdminDTO d) {
        return Admin.builder()
                .fullName(d.getFullName())
                .email(d.getEmail())
                .password("changeme") // placeholder until security hashed
                .build();
    }
}
