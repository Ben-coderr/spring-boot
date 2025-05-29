package com.school.repository;
import com.school.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> { // lesson store
    java.util.List<Lesson> findByTeacher_Id(Long teacherId);
    java.util.List<Lesson> findBySchoolClass_Id(Long classId);
    java.util.List<Lesson> findBySubject_Id(Long subjectId);
}
