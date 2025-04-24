package com.school.schoolclass.model;

import java.util.ArrayList;
import java.util.List;

import com.school.grade.model.Grade;
import com.school.student.model.Student;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolClass {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Grade grade;


    @Builder.Default        
    @OneToMany(mappedBy = "schoolClass",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Student> students = new ArrayList<>();
}

