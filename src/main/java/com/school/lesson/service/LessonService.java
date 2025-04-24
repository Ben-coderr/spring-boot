package com.school.lesson.service;

import com.school.common.exception.NotFoundException;
import com.school.lesson.dto.LessonDTO;
import com.school.lesson.model.Lesson;
import com.school.lesson.repository.LessonRepository;
import com.school.subject.repository.SubjectRepository;
import com.school.teacher.repository.TeacherRepository;
import com.school.schoolclass.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository repo;
    private final SubjectRepository subjectRepo;
    private final TeacherRepository teacherRepo;
    private final SchoolClassRepository classRepo;

    public List<LessonDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public LessonDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Lesson " + id + " not found")));
    }

    public LessonDTO create(LessonDTO dto) { return toDto(repo.save(toEntity(dto))); }

    public LessonDTO update(Long id, LessonDTO dto) {
        Lesson l = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Lesson " + id + " not found"));
        l.setTopic(dto.getTopic());
        l.setLessonDate(dto.getLessonDate());
        l.setSubject(subjectRepo.findById(dto.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject " + dto.getSubjectId() + " not found")));
        l.setTeacher(teacherRepo.findById(dto.getTeacherId())
                .orElseThrow(() -> new NotFoundException("Teacher " + dto.getTeacherId() + " not found")));
        l.setSchoolClass(classRepo.findById(dto.getClassId())
                .orElseThrow(() -> new NotFoundException("Class " + dto.getClassId() + " not found")));
        return toDto(repo.save(l));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Lesson " + id + " not found");
        repo.deleteById(id);
    }
    public List<LessonDTO> findBySubject(Long subjectId) {
        List<Lesson> lessons = repo.findAllBySubject_Id(subjectId);
        if (lessons.isEmpty()) {
            throw new NotFoundException("No lessons found for subject " + subjectId);
        }
        return lessons.stream().map(this::toDto).toList();
    }
    /* ---------- mapping helpers ---------- */
    private LessonDTO toDto(Lesson l) {
        return LessonDTO.builder()
                .id(l.getId())
                .topic(l.getTopic())
                .lessonDate(l.getLessonDate())
                .subjectId(l.getSubject().getId())
                .teacherId(l.getTeacher().getId())
                .classId(l.getSchoolClass().getId())
                .build();
    }

    private Lesson toEntity(LessonDTO d) {
        return Lesson.builder()
                .topic(d.getTopic())
                .lessonDate(d.getLessonDate())
                .subject(subjectRepo.findById(d.getSubjectId())
                        .orElseThrow(() -> new NotFoundException("Subject " + d.getSubjectId() + " not found")))
                .teacher(teacherRepo.findById(d.getTeacherId())
                        .orElseThrow(() -> new NotFoundException("Teacher " + d.getTeacherId() + " not found")))
                .schoolClass(classRepo.findById(d.getClassId())
                        .orElseThrow(() -> new NotFoundException("Class " + d.getClassId() + " not found")))
                .build();
    }
}
