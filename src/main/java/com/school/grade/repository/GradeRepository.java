package com.school.grade.repository;

import com.school.grade.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {   boolean existsByLevel(Integer level); }
