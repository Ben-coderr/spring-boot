package com.school.repository;
import com.school.model.Attendance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    long countByStudentId(Long studentId);                     // total rows
    long countByStudentIdAndStatus(Long studentId, String s);  // filter PRESENT
    List<Attendance> findByStudentId(Long studentId);
}
