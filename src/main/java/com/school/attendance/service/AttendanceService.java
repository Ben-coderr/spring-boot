package com.school.attendance.service;

import com.school.common.exception.NotFoundException;
import com.school.attendance.dto.AttendanceDTO;
import com.school.attendance.model.Attendance;
import com.school.attendance.repository.AttendanceRepository;
import com.school.student.repository.StudentRepository;
import com.school.lesson.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;




@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceRepository repo;
    private final StudentRepository studentRepo;
    private final LessonRepository lessonRepo;
        @Transactional(readOnly = true)
    public List<AttendanceDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public AttendanceDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Attendance " + id + " not found")));
    }

    public AttendanceDTO create(AttendanceDTO dto) { return toDto(repo.save(toEntity(dto))); }

    public AttendanceDTO update(Long id, AttendanceDTO dto) {
        Attendance a = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Attendance " + id + " not found"));
        a.setStatus(dto.getStatus());
        a.setStudent(studentRepo.findById(dto.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student " + dto.getStudentId() + " not found")));
        a.setLesson(lessonRepo.findById(dto.getLessonId())
                .orElseThrow(() -> new NotFoundException("Lesson " + dto.getLessonId() + " not found")));
        return toDto(repo.save(a));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Attendance " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- helpers ---------- */
    private AttendanceDTO toDto(Attendance a) {
        return AttendanceDTO.builder()
                .id(a.getId())
                .status(a.getStatus())
                .studentId(a.getStudent().getId())
                .lessonId(a.getLesson().getId())
                .build();
    }

    private Attendance toEntity(AttendanceDTO d) {
        return Attendance.builder()
                .status(d.getStatus())
                .student(studentRepo.findById(d.getStudentId())
                        .orElseThrow(() -> new NotFoundException("Student " + d.getStudentId() + " not found")))
                .lesson(lessonRepo.findById(d.getLessonId())
                        .orElseThrow(() -> new NotFoundException("Lesson " + d.getLessonId() + " not found")))
                .build();
    }
}
