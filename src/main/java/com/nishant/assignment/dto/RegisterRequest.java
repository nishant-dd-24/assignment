package com.nishant.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Registration request payload")
public record RegisterRequest(
        @NotBlank(message = "Name is required")
        @Schema(description = "User full name", example = "Nishant Kumar")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Valid email required")
        @Schema(description = "Unique user email", example = "nishant@example.com")
        String email,

        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "Password with minimum length 6", example = "secret123")
        String password
) {
}
