package com.nishant.assignment.dto;

import com.nishant.assignment.entity.Task;
import com.nishant.assignment.entity.TaskStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        String ownerName,
        LocalDateTime createdAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(), task.getTitle(), task.getDescription(),
                task.getStatus(), task.getOwner().getName(), task.getCreatedAt()
        );
    }
}
