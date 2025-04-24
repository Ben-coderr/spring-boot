package com.school.event.service;

import com.school.common.exception.NotFoundException;
import com.school.event.dto.EventDTO;
import com.school.event.model.Event;
import com.school.event.repository.EventRepository;
import com.school.schoolclass.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repo;
    private final SchoolClassRepository classRepo;

    public List<EventDTO> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }

    public EventDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Event " + id + " not found")));
    }

    public EventDTO create(EventDTO dto) { return toDto(repo.save(toEntity(dto))); }

    public EventDTO update(Long id, EventDTO dto) {
        Event e = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Event " + id + " not found"));
        e.setTitle(dto.getTitle());
        e.setDescription(dto.getDescription());
        e.setStartsAt(dto.getStartsAt());
        e.setEndsAt(dto.getEndsAt());
        e.setSchoolClass(resolveClass(dto.getClassId()));
        return toDto(repo.save(e));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Event " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- helpers ---------- */
    private EventDTO toDto(Event e) {
        return EventDTO.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .startsAt(e.getStartsAt())
                .endsAt(e.getEndsAt())
                .classId(e.getSchoolClass() == null ? null : e.getSchoolClass().getId())
                .build();
    }

    private Event toEntity(EventDTO d) {
        return Event.builder()
                .title(d.getTitle())
                .description(d.getDescription())
                .startsAt(d.getStartsAt())
                .endsAt(d.getEndsAt())
                .schoolClass(resolveClass(d.getClassId()))
                .build();
    }

    private com.school.schoolclass.model.SchoolClass resolveClass(Long id) {
        if (id == null) return null;
        return classRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Class " + id + " not found"));
    }
}
