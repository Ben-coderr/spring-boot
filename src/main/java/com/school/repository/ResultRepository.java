package com.school.repository;

import com.school.model.Result;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResultRepository extends JpaRepository<Result, Long> {
    @Query("select avg(r.score) from Result r where r.student.id = :sid")
    Double averageForStudent(@Param("sid") Long studentId);


    @Query("""
           select avg(r.score)
           from   Result   r
           join   r.exam   e
           join   e.lesson l
           where  r.student.id = :sid
             and  l.subject.id = :subId
           """)
    Double averageForStudentAndSubject(@Param("sid") Long studentId,
                                       @Param("subId") Long subjectId);
    @Query("""
            select r.student.id, avg(r.score)
            from Result r
            where r.student.schoolClass.id = :cls
            group by r.student.id
            order by avg(r.score) desc
           """)
    List<Object[]> findClassRanking(@Param("cls") Long classId);
}