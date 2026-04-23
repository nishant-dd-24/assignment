package com.nishant.assignment.exception.custom;

import com.nishant.assignment.exception.ErrorCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

    public UnauthorizedException(String message) {
        super(message);
    }

}
