package project.edusphere.event.model;

import project.edusphere.schoolclass.model.SchoolClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;             // "Sports Day"

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    /** Null ⇒ whole-school event; non-null ⇒ only that class. */
    @ManyToOne(fetch = FetchType.LAZY)
    private SchoolClass schoolClass;
}
