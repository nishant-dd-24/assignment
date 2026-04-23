package com.nishant.assignment.service;

import com.nishant.assignment.dto.*;
import com.nishant.assignment.entity.*;
import com.nishant.assignment.exception.ExceptionUtil;
import com.nishant.assignment.repository.UserRepository;
import com.nishant.assignment.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ExceptionUtil exceptionUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email()))
            throw exceptionUtil.duplicate("Email already registered");

        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> exceptionUtil.unauthorized("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPassword()))
            throw exceptionUtil.unauthorized("Invalid credentials");

        String token = jwtUtil.generateToken(user.getEmail(),  user.getRole());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}