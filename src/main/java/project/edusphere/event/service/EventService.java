package project.edusphere.event.service;

import project.edusphere.common.exception.NotFoundException;
import project.edusphere.event.dto.EventDTO;
import project.edusphere.event.model.Event;
import project.edusphere.event.repository.EventRepository;
import project.edusphere.schoolclass.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository repo;
    private final SchoolClassRepository classRepo;
    @Transactional(readOnly = true)
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

    private project.edusphere.schoolclass.model.SchoolClass resolveClass(Long id) {
        if (id == null) return null;
        return classRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Class " + id + " not found"));
    }
}
