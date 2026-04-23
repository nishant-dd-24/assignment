package com.nishant.assignment.exception.security;

import com.nishant.assignment.exception.response.ErrorResponse;
import com.nishant.assignment.exception.response.ErrorResponseFactory;
import com.nishant.assignment.exception.response.ErrorResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ErrorResponseWriter errorResponseWriter;
    private final ErrorResponseFactory errorResponseFactory;

    @Override
    public void handle(@NonNull HttpServletRequest request,
                       @NonNull HttpServletResponse response,
                       @NonNull AccessDeniedException ex) throws IOException {
        ErrorResponse error = errorResponseFactory.forbidden("Access Denied", request);
        errorResponseWriter.write(response, error);
    }
}
