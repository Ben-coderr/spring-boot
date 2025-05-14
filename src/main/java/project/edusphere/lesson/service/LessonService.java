package project.edusphere.lesson.service;

import java.time.LocalTime;

import project.edusphere.common.exception.NotFoundException;
import project.edusphere.lesson.dto.LessonDTO;
import project.edusphere.lesson.model.Lesson;
import project.edusphere.lesson.repository.LessonRepository;
import project.edusphere.subject.repository.SubjectRepository;
import project.edusphere.teacher.repository.TeacherRepository;
import project.edusphere.schoolclass.repository.SchoolClassRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import project.edusphere.lesson.model.DayOfWeek;



@Service
@RequiredArgsConstructor
@Transactional
public class LessonService {

    private final LessonRepository repo;
    private final SubjectRepository subjectRepo;
    private final TeacherRepository teacherRepo;
    private final SchoolClassRepository classRepo;
    @Transactional(readOnly = true)
    public List<LessonDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public LessonDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Lesson " + id + " not found")));
    }

    public LessonDTO create(LessonDTO dto) {

        
        if (dto.getStartTime().compareTo(dto.getEndTime()) >= 0) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }

        
        return toDto(repo.save(toEntity(dto)));
    }

    public LessonDTO update(Long id, LessonDTO dto) {
        if (dto.getStartTime().compareTo(dto.getEndTime()) >= 0) {
                throw new IllegalArgumentException("startTime must be before endTime");
                }
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
                .day(l.getDay().name())
                .startTime(l.getStartTime().toString())
                .endTime(l.getEndTime().toString())
                .subjectId(l.getSubject().getId())
                .teacherId(l.getTeacher().getId())
                .classId(l.getSchoolClass().getId())
                .build();
    }

    private Lesson toEntity(LessonDTO d) {
        return Lesson.builder()
                .topic(d.getTopic())
                .lessonDate(d.getLessonDate())
                .day(DayOfWeek.valueOf(d.getDay()))
                .startTime(LocalTime.parse(d.getStartTime()))
                .endTime(LocalTime.parse(d.getEndTime()))
                
                .subject(subjectRepo.findById(d.getSubjectId())
                        .orElseThrow(() -> new NotFoundException("Subject " + d.getSubjectId() + " not found")))
                .teacher(teacherRepo.findById(d.getTeacherId())
                        .orElseThrow(() -> new NotFoundException("Teacher " + d.getTeacherId() + " not found")))
                .schoolClass(classRepo.findById(d.getClassId())
                        .orElseThrow(() -> new NotFoundException("Class " + d.getClassId() + " not found")))
                .build();
    }
    
}
