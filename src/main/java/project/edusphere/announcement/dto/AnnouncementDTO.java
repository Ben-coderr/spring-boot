package project.edusphere.announcement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime publishedAt;
    private Long classId;          // null = global
}
