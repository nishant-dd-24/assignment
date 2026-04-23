package com.nishant.assignment.exception.response;


import com.nishant.assignment.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Schema(description = "Standard API error response")
public record ErrorResponse(
        @Schema(description = "Request path", example = "/api/v1/tasks/42")
        String path,
        @Schema(description = "HTTP method", example = "GET")
        String method,
        @Schema(description = "HTTP status code", example = "404")
        int status,
        @Schema(description = "Human-readable error message", example = "Task not found")
        String message,
        @Schema(description = "Application-specific error code", example = "RESOURCE_NOT_FOUND")
        ErrorCode errorCode,
        @Schema(description = "Server timestamp when error occurred", example = "2026-04-23T13:00:00")
        LocalDateTime timestamp,
        @Schema(description = "Validation error map: field -> message", example = "{\"email\":\"Valid email required\"}")
        Map<String, String> errors
) {
}