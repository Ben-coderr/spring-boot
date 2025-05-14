package com.school.exam.service;

import com.school.common.exception.NotFoundException;
import com.school.exam.dto.ExamDTO;
import com.school.exam.model.Exam;
import com.school.exam.repository.ExamRepository;
import com.school.lesson.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository repo;
    private final LessonRepository lessonRepo;
    @Transactional(readOnly = true)
    public List<ExamDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public ExamDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Exam " + id + " not found")));
    }

    public ExamDTO create(ExamDTO dto) { return toDto(repo.save(toEntity(dto))); }

    public ExamDTO update(Long id, ExamDTO dto) {
        Exam e = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Exam " + id + " not found"));
        e.setTitle(dto.getTitle());
        e.setExamDate(dto.getExamDate());
        e.setLesson(lessonRepo.findById(dto.getLessonId())
                .orElseThrow(() -> new NotFoundException("Lesson " + dto.getLessonId() + " not found")));
        return toDto(repo.save(e));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Exam " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- mapping helpers ---------- */
    private ExamDTO toDto(Exam e) {
        return ExamDTO.builder()
                .id(e.getId())
                .title(e.getTitle())
                .examDate(e.getExamDate())
                .lessonId(e.getLesson().getId())
                .build();
    }

    private Exam toEntity(ExamDTO d) {
        return Exam.builder()
                .title(d.getTitle())
                .examDate(d.getExamDate())
                .lesson(lessonRepo.findById(d.getLessonId())
                        .orElseThrow(() -> new NotFoundException("Lesson " + d.getLessonId() + " not found")))
                .build();
    }
}
