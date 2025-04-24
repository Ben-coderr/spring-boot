package com.com.school.grade.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Grade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** e.g. "Grade 1", "Year 7", "Terminale" … */
    @Column(nullable = false, unique = true)
    private String name;
}
