package com.school.dto;

import com.school.model.Attendance;

import java.time.LocalDate;

public record AttendanceDto(Long id, LocalDate date, String status,
                            Long lessonId) {
    public static AttendanceDto from(Attendance attendance) {
        Long lid = (attendance.getLesson()  != null) ? attendance.getLesson().getId()  : null;
        return new AttendanceDto(attendance.getId(), attendance.getDate(), attendance.getStatus(), lid);
    }
}
