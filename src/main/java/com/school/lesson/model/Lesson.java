package com.school.lesson.model;
import java.time.LocalTime;
import com.school.lesson.model.DayOfWeek;
import com.school.subject.model.Subject;
import com.school.teacher.model.Teacher;
import com.school.schoolclass.model.SchoolClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lesson {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;                   // e.g. "Fractions"

    private LocalDate lessonDate;           // optional

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id")       
    private SchoolClass schoolClass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DayOfWeek day;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;
}
