package com.school.lesson.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.lesson.model.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllBySubject_Id(Long subjectId);
}