package com.school.system.models.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// SchoolClass.java
@Entity
@Table(name = "classes")
@Getter
@Setter
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private Teacher supervisor;

    @OneToMany(mappedBy = "schoolClass")
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "schoolClass")
    private List<Student> students = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @OneToMany(mappedBy = "schoolClass")
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "schoolClass")
    private List<Announcement> announcements = new ArrayList<>();
}