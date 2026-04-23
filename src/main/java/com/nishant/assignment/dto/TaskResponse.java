package com.nishant.assignment.dto;

import com.nishant.assignment.entity.Task;
import com.nishant.assignment.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Task response payload")
public record TaskResponse(
        @Schema(description = "Task ID", example = "42")
        Long id,
        @Schema(description = "Task title", example = "Prepare API documentation")
        String title,
        @Schema(description = "Task description", example = "Add OpenAPI annotations for all endpoints")
        String description,
        @Schema(description = "Current task status", example = "IN_PROGRESS")
        TaskStatus status,
        @Schema(description = "Task owner name", example = "Nishant Kumar")
        String ownerName,
        @Schema(description = "Task creation timestamp", example = "2026-04-23T10:15:30")
        LocalDateTime createdAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(), task.getTitle(), task.getDescription(),
                task.getStatus(), task.getOwner().getName(), task.getCreatedAt()
        );
    }
}
