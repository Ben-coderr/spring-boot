package com.school.system.models.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.security.auth.Subject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Teacher.java
@Entity
@Table(name = "teachers")
@Getter
@Setter
public class Teacher extends BaseEntity {
    @Id
    private String id;

    @Column(unique = true)
    private String username;

    private String name;
    private String surname;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String address;
    private String img;
    private String bloodType;

    @Enumerated(EnumType.STRING)
    private UserSex sex;

    private LocalDate birthday;

    @ManyToMany
    @JoinTable(
            name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private List<SchoolSubject> subjects = new ArrayList<>();

    @OneToMany(mappedBy = "teacher")
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "supervisor")
    private List<SchoolClass> supervisedClasses = new ArrayList<>();
}