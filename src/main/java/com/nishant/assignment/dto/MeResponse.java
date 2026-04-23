package com.nishant.assignment.dto;

import com.nishant.assignment.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current authenticated user details")
public record MeResponse(
        @Schema(description = "User ID", example = "1")
        Long id,
        @Schema(description = "User full name", example = "Nishant Kumar")
        String name,
        @Schema(description = "User email", example = "user@example.com")
        String email,
        @Schema(description = "Current role", example = "USER")
        Role role
) {
}

