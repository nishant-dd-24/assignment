package com.nishant.assignment.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task lifecycle status")
public enum TaskStatus {
    @Schema(description = "Task has not been started")
    TODO,
    @Schema(description = "Task is currently being worked on")
    IN_PROGRESS,
    @Schema(description = "Task has been completed")
    DONE
}