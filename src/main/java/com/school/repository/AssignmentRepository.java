package com.school.repository;
import com.school.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // find all assignments for a given lesson
    java.util.List<Assignment> findByLesson_Id(Long lessonId);

    // assignments given by a teacher (via lesson)
    java.util.List<Assignment> findByLesson_Teacher_Id(Long teacherId);

    // assignments for a specific class
    java.util.List<Assignment> findByLesson_SchoolClass_Id(Long classId);
}
