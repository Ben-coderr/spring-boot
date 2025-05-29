package com.school.repository;
import com.school.model.Teacher;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> { // teacher table
    Optional<Teacher> findByUser_Id(Long userId);
    java.util.List<Teacher> findBySubject_Id(Long subjectId);
}
