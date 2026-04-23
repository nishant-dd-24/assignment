package com.nishant.assignment.exception;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nishant.assignment.exception.custom.CustomBadRequestException;
import com.nishant.assignment.exception.custom.DuplicateResourceException;
import com.nishant.assignment.exception.custom.ResourceNotFoundException;
import com.nishant.assignment.exception.custom.UnauthorizedException;
import com.nishant.assignment.exception.response.ErrorResponse;
import com.nishant.assignment.exception.response.ErrorResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );
        return errorResponseFactory.build(HttpStatus.BAD_REQUEST, "Validation Failed", ErrorCode.VALIDATION_FAILED, errors, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return errorResponseFactory.build(HttpStatus.BAD_REQUEST, "Invalid value provided: " + ex.getMessage(), ErrorCode.ILLEGAL_ARGUMENT, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEnumError(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Invalid request";
        if (ex.getCause() instanceof InvalidFormatException e) {
            String fieldName = (e.getPath() != null && !e.getPath().isEmpty())
                    ? e.getPath().getFirst().getFieldName()
                    : "unknown";
            Object invalidValue = e.getValue();
            Class<?> targetType = e.getTargetType();
            if (targetType != null && targetType.isEnum()) {
                Object[] allowedValues = targetType.getEnumConstants();
                message = String.format(
                        "Invalid value '%s' for field '%s'. Allowed values: %s",
                        invalidValue,
                        fieldName,
                        Arrays.toString(allowedValues)
                );
            } else if (targetType != null) {
                message = String.format(
                        "Invalid value '%s' for field '%s'. Expected type: %s",
                        invalidValue,
                        fieldName,
                        targetType.getSimpleName()
                );
            }
        }
        return errorResponseFactory.build(HttpStatus.BAD_REQUEST, message, ErrorCode.INVALID_ENUM_VALUE, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return errorResponseFactory.build(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getErrorCode(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        return errorResponseFactory.build(HttpStatus.CONFLICT, ex.getMessage(), ex.getErrorCode(), request);
    }

    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(Exception ex, HttpServletRequest request) {
        return errorResponseFactory.forbidden(ex.getMessage(), request);
    }


    @ExceptionHandler({AuthenticationException.class, UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(Exception ex, HttpServletRequest request) {
        return errorResponseFactory.unauthorized(ex.getMessage(), request);
    }

    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(CustomBadRequestException ex, HttpServletRequest request) {
        return errorResponseFactory.build(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrorCode(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        assert ex.getRequiredType() != null;
        String message = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'. Expected type: " + ex.getRequiredType().getSimpleName();
        return errorResponseFactory.build(HttpStatus.BAD_REQUEST, message, ErrorCode.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String supported = ex.getSupportedHttpMethods() == null
                ? ""
                : ex.getSupportedHttpMethods().stream()
                  .map(HttpMethod::name)
                  .reduce((a, b) -> a + ", " + b)
                  .orElse("");
        String message = "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint. Supported methods: " + supported;
        return errorResponseFactory.build(HttpStatus.METHOD_NOT_ALLOWED, message, ErrorCode.BAD_REQUEST, request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFound(HttpServletRequest request) {
        return errorResponseFactory.build(HttpStatus.NOT_FOUND, "The requested resource was not found", ErrorCode.NOT_FOUND, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        return errorResponseFactory.build(HttpStatus.CONFLICT, "Data integrity violation: " + ex.getMostSpecificCause().getMessage(), ErrorCode.DATA_INTEGRITY_VIOLATION, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(HttpServletRequest request) {
        return errorResponseFactory.build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", ErrorCode.INTERNAL_ERROR, request);
    }
}
