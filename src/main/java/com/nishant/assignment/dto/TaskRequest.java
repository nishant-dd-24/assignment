package com.nishant.assignment.dto;

import com.nishant.assignment.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Task create/update request payload")
public record TaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100)
        @Schema(description = "Task title", example = "Prepare API documentation")
        String title,

        @Size(max = 500)
        @Schema(description = "Task description", example = "Add OpenAPI annotations for controllers and DTOs")
        String description,

        @Schema(description = "Task status. Defaults to TODO when omitted during creation", example = "TODO")
        TaskStatus status
) {
}
