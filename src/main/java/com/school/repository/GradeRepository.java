package com.school.repository;
import com.school.model.Grade;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByLevel(Integer level);
}
