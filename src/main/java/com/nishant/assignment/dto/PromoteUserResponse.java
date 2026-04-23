package com.nishant.assignment.dto;

import com.nishant.assignment.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload for admin user promotion")
public record PromoteUserResponse(
        @Schema(description = "Promoted user ID", example = "2")
        Long id,
        @Schema(description = "Promoted user full name", example = "Ananya Singh")
        String name,
        @Schema(description = "Promoted user email", example = "ananya@example.com")
        String email,
        @Schema(description = "Updated role after promotion", example = "ADMIN")
        Role role
) {
}

