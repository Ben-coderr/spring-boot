package com.school.exam.model;

import com.school.lesson.model.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;            // e.g. "Mid-term Algebra"

    private LocalDate examDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Lesson lesson;
}
