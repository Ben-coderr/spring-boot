package com.school.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminDTO {

    @Schema(description = "Auto-generated unique ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Full name of the admin", example = "Jane Doe", required = true)
    private String fullName;

    @Schema(description = "Email address (must be unique)", example = "jane@school.com", required = true)
    private String email;

    @Schema(description = "Password (min 8 characters)", example = "securePassword123", required = true)
    private String password;
}