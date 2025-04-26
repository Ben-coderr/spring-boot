package com.school.lesson.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LessonDTO {
    private Long id;
    private String topic;
    private LocalDate lessonDate;
    private Long subjectId;
    private Long teacherId;
    private Long classId;
    private String day;             
    private String startTime;       
    private String endTime;        
}
