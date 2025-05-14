package project.edusphere.assignment.model;

import project.edusphere.lesson.model.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Assignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;          // e.g. "Homework #4"

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Lesson lesson;
}
