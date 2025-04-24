package com.yourorg.school.grade.repository;

import com.yourorg.school.grade.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> { }
