package com.school.repository;
import com.school.model.Student;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> { // store students
    long countBySchoolClass_Id(Long clzId);
    List<Student> findBySchoolClass_Id(Long classId);
    List<Student> findByParent_Id(Long parentId);

    @Query("""
            select coalesce(avg(r.score),0)
            from   Result r
            where  r.student.id = :sid
           """)
    Double averageScore(@Param("sid") Long studentId);
    Optional<Student> findByUser_Id(Long userId);
    
}
