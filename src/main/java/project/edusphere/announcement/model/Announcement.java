package project.edusphere.announcement.model;

import project.edusphere.schoolclass.model.SchoolClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Announcement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;                  // "Exam timetable"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;                // body text / HTML

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    /** Null ⇒ visible to everyone. */
    @ManyToOne(fetch = FetchType.LAZY)
    private SchoolClass schoolClass;
}
