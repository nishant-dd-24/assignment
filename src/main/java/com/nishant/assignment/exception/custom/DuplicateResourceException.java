package com.nishant.assignment.exception.custom;


import com.nishant.assignment.exception.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.ALREADY_EXISTS;

    public DuplicateResourceException(String message) {
        super(message);
    }
}
