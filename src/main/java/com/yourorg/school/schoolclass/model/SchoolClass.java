package com.yourorg.school.schoolclass.model;

import com.yourorg.school.grade.model.Grade;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolClass {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;          // e.g. "1-A", "7-B"

    /** Each class belongs to one grade (Year). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Grade grade;
}
