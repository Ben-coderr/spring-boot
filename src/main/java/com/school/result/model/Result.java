package com.school.result.model;

import com.school.student.model.Student;
import com.school.exam.model.Exam;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Result {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double score;                // e.g. 15.5

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Exam exam;
}
