package com.school.dto;

import com.school.model.Attendance;

import java.time.LocalDate;

public record AttendanceDto(Long id, LocalDate date, String status,
                            Long studentId, Long lessonId) {
    public static AttendanceDto from(Attendance a) {
        Long sid = (a.getStudent() != null) ? a.getStudent().getId() : null;
        Long lid = (a.getLesson()  != null) ? a.getLesson().getId()  : null;
        return new AttendanceDto(a.getId(), a.getDate(), a.getStatus(), sid, lid);
    }
}
