package com.nishant.assignment.exception.security;

import com.nishant.assignment.exception.response.ErrorResponse;
import com.nishant.assignment.exception.response.ErrorResponseFactory;
import com.nishant.assignment.exception.response.ErrorResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ErrorResponseWriter errorResponseWriter;
    private final ErrorResponseFactory errorResponseFactory;

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException ex) throws IOException {
        ErrorResponse error = errorResponseFactory.unauthorized("Authentication required", request);
        errorResponseWriter.write(response, error);
    }
}
