package com.school.system.models.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Lesson.java
@Entity
@Getter
@Setter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Day day;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SchoolSubject subject;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToMany(mappedBy = "lesson")
    private List<Exam> exams = new ArrayList<>();

    @OneToMany(mappedBy = "lesson")
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "lesson")
    private List<Attendance> attendances = new ArrayList<>();
}
