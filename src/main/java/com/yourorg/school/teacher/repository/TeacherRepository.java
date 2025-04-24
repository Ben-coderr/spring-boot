package com.yourorg.school.teacher.repository;

import com.yourorg.school.teacher.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> { }
