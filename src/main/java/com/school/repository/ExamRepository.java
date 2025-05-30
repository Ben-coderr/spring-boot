package com.school.repository;
import com.school.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> { // exam table
    // exams for a lesson
    java.util.List<Exam> findByLesson_Id(Long lessonId);

    // exams given by a teacher (via lesson)
    java.util.List<Exam> findByLesson_Teacher_Id(Long teacherId);

    // exams for a specific class
    java.util.List<Exam> findByLesson_SchoolClass_Id(Long classId);
}
