package com.school.repository;
import com.school.model.Student;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {
    long countBySchoolClass_Id(Long clzId);
    List<Student> findBySchoolClass_Id(Long classId);

    @Query("""
            select coalesce(avg(r.score),0)
            from   Result r
            where  r.student.id = :sid
           """)
    Double averageScore(@Param("sid") Long studentId);
    
}
