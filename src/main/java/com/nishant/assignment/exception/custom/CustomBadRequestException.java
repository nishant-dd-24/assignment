package com.nishant.assignment.exception.custom;

import com.nishant.assignment.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CustomBadRequestException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.BAD_REQUEST;

    public CustomBadRequestException(String message) {
        super(message);
    }
}
