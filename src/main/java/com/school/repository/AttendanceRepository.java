package com.school.repository;
import com.school.model.Attendance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> { // attendance records
    long countByStudentId(Long studentId);                     // total rows
    long countByStudentIdAndStatus(Long studentId, String s);  // filter PRESENT
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByLesson_Id(Long lessonId);

    // all attendance rows for a class
    List<Attendance> findByStudent_SchoolClass_Id(Long classId);

    // attendance rows for lessons taught by a teacher
    java.util.List<Attendance> findByLesson_Teacher_Id(Long teacherId);
}
