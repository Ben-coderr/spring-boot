package com.school.system.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Grade.java
@Entity
@Getter
@Setter
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer level;

    @OneToMany(mappedBy = "grade")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "grade")
    private List<SchoolClass> classes = new ArrayList<>();
}