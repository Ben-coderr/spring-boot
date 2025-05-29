package com.school.dto;

import com.school.model.Attendance;

import java.time.LocalDate;

public record AttendanceDto(Long id, LocalDate date, String status,
                            Long studentId, Long lessonId) {
    public static AttendanceDto from(Attendance attendance) {
        Long sid = (attendance.getStudent() != null) ? attendance.getStudent().getId() : null;
        Long lid = (attendance.getLesson()  != null) ? attendance.getLesson().getId()  : null;
        return new AttendanceDto(attendance.getId(), attendance.getDate(), attendance.getStatus(), sid, lid);
    }
}
