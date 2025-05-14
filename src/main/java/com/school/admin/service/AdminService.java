package com.school.admin.service;

import com.school.admin.dto.AdminDTO;
import com.school.admin.model.Admin;
import com.school.admin.repository.AdminRepository;
import com.school.common.enums.Role;
import com.school.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;




@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final AdminRepository repo;
    private final PasswordEncoder encoder;

    @Transactional(readOnly = true)
    public List<AdminDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public AdminDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin " + id + " not found")));
    }

    public AdminDTO create(AdminDTO dto) {
        Admin saved = repo.save(
            Admin.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(encoder.encode(requirePwd(dto.getPassword()))) // encoder @RequiredArgsConstructor
                .role(Role.ADMIN)
                .build());
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
    private String requirePwd(String raw) {
        if (raw == null || raw.isBlank())
            throw new IllegalArgumentException("password is required");
        return raw;
    }
}
