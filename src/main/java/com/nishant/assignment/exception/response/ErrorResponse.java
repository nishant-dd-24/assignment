package com.nishant.assignment.exception.response;


import com.nishant.assignment.exception.ErrorCode;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErrorResponse(
        String traceId,
        String path,
        String method,
        int status,
        String message,
        ErrorCode errorCode,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
}