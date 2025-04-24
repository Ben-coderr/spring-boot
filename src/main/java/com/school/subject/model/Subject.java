package com.school.subject.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"subject\"")                    // quoted → safe on all SQL engines
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;                       // e.g. "Mathematics", "Physics"
}
