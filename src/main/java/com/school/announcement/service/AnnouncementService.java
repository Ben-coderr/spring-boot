package com.school.announcement.service;

import com.school.common.exception.NotFoundException;
import com.school.announcement.dto.AnnouncementDTO;
import com.school.announcement.model.Announcement;
import com.school.announcement.repository.AnnouncementRepository;
import com.school.schoolclass.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementService {

    private final AnnouncementRepository repo;
    private final SchoolClassRepository classRepo;
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public AnnouncementDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Announcement " + id + " not found")));
    }

    public AnnouncementDTO create(AnnouncementDTO dto) {
        if (dto.getPublishedAt() == null) dto.setPublishedAt(LocalDateTime.now());
        return toDto(repo.save(toEntity(dto)));
    }

    public AnnouncementDTO update(Long id, AnnouncementDTO dto) {
        Announcement a = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Announcement " + id + " not found"));
        a.setTitle(dto.getTitle());
        a.setContent(dto.getContent());
        a.setPublishedAt(dto.getPublishedAt());
        a.setSchoolClass(resolveClass(dto.getClassId()));
        return toDto(repo.save(a));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Announcement " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- helpers ---------- */
    private AnnouncementDTO toDto(Announcement a) {
        return AnnouncementDTO.builder()
                .id(a.getId())
                .title(a.getTitle())
                .content(a.getContent())
                .publishedAt(a.getPublishedAt())
                .classId(a.getSchoolClass() == null ? null : a.getSchoolClass().getId())
                .build();
    }

    private Announcement toEntity(AnnouncementDTO d) {
        return Announcement.builder()
                .title(d.getTitle())
                .content(d.getContent())
                .publishedAt(d.getPublishedAt())
                .schoolClass(resolveClass(d.getClassId()))
                .build();
    }

    private com.school.schoolclass.model.SchoolClass resolveClass(Long id) {
        if (id == null) return null;
        return classRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Class " + id + " not found"));
    }
}
