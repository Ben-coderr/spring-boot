package com.school.dto;

import com.school.model.Lesson;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonDto(Long id, String topic, LocalDate lessonDate, String day,
                        LocalTime startTime, LocalTime endTime,
                        Long subjectId, Long teacherId, Long classId) {
    public static LessonDto from(Lesson lesson) {
        Long sid = (lesson.getSubject() != null) ? lesson.getSubject().getId() : null;
        Long tid = (lesson.getTeacher() != null) ? lesson.getTeacher().getId() : null;
        Long cid = (lesson.getSchoolClass() != null) ? lesson.getSchoolClass().getId() : null;
        return new LessonDto(lesson.getId(), lesson.getTopic(), lesson.getLessonDate(), lesson.getDay(),
                lesson.getStartTime(), lesson.getEndTime(), sid, tid, cid);
    }
}
