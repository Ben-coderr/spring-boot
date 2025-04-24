package com.school.attendance.dto;

import com.school.attendance.model.Attendance.Status;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceDTO {
    private Long id;
    private Status status;
    private Long studentId;
    private Long lessonId;
}
