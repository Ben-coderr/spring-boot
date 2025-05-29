package com.school.dto;

import com.school.model.Attendance;
import com.school.model.Student;
import com.school.model.Lesson;
import com.school.repository.StudentRepository;
import com.school.repository.LessonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AttendanceMapper {
    public static AttendanceDto toDto(Attendance attendance) {
        return AttendanceDto.from(attendance);
    }

    public static Attendance toEntity(AttendanceDto dto,
                                     StudentRepository students,
                                     LessonRepository lessons) {
        Attendance attendance = new Attendance();
        copyOnWrite(dto, attendance, students, lessons);
        return attendance;
    }

    public static void copyOnWrite(AttendanceDto dto,
                                   Attendance target,
                                   StudentRepository students,
                                   LessonRepository lessons) {
        if (dto.status() != null) target.setStatus(dto.status());
        if (dto.date() != null) target.setDate(dto.date());

        if (dto.studentId() != null) {
            Student student = students.findById(dto.studentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "student not found"));
            target.setStudent(student);
        }

        if (dto.lessonId() != null) {
            Lesson lesson = lessons.findById(dto.lessonId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "lesson not found"));
            target.setLesson(lesson);
        }
    }

    private AttendanceMapper() {}
}
