package com.school.repository;
import com.school.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> { // school classes
    java.util.List<SchoolClass> findByGrade_Id(Long gradeId);
}
