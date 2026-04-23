package com.nishant.assignment.dto;

import com.nishant.assignment.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100)
        String title,

        @Size(max = 500)
        String description,

        TaskStatus status
) {
}
