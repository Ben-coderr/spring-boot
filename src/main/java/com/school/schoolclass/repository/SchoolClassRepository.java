package com.school.schoolclass.repository;

import com.school.schoolclass.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SchoolClass> findById(Long id);
    List<SchoolClass> findByGradeId(Long gradeId);
}

