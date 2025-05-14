package project.edusphere.schoolclass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolClassDTO {
    private Long id;
    private String name;
    private Long gradeId;
    private Integer capacity;
    private Long supervisorId;
}
