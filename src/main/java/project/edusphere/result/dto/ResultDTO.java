package project.edusphere.result.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResultDTO {
    private Long id;
    private Double score;
    private Long studentId;
    private Long examId;
}
