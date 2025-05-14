package project.edusphere.admin.model;

import project.edusphere.common.audit.Auditable;
import project.edusphere.common.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Admin extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Auto-generated unique ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Full name of the admin", example = "Jane Doe")
    private String fullName;

    @Column(nullable = false, unique = true)
    @Schema(description = "Email address (unique)", example = "jane@school.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Hashed password (never exposed in responses)")
    private String password;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "Role of the user (default: ADMIN)", example = "ADMIN")
    private Role role = Role.ADMIN;
}