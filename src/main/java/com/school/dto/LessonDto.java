package com.school.dto;

import com.school.model.Lesson;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonDto(Long id, String topic, LocalDate lessonDate, String day,
                        LocalTime startTime, LocalTime endTime,
                        Long subjectId, Long teacherId, Long classId) {
    public static LessonDto from(Lesson l) {
        Long sid = (l.getSubject() != null) ? l.getSubject().getId() : null;
        Long tid = (l.getTeacher() != null) ? l.getTeacher().getId() : null;
        Long cid = (l.getSchoolClass() != null) ? l.getSchoolClass().getId() : null;
        return new LessonDto(l.getId(), l.getTopic(), l.getLessonDate(), l.getDay(),
                l.getStartTime(), l.getEndTime(), sid, tid, cid);
    }
}
