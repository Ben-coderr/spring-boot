package com.school.schoolclass.repository;

import com.school.schoolclass.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findByGradeId(Long gradeId);
}
