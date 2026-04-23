package com.nishant.assignment.service;

import com.nishant.assignment.dto.*;
import com.nishant.assignment.entity.*;
import com.nishant.assignment.exception.ExceptionUtil;
import com.nishant.assignment.repository.UserRepository;
import com.nishant.assignment.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private ExceptionUtil exceptionUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateUserAndReturnToken() {
        RegisterRequest req = new RegisterRequest("Nishant", "test@mail.com", "password");

        when(userRepository.existsByEmail(req.email())).thenReturn(false);
        when(passwordEncoder.encode(req.password())).thenReturn("hashed");
        when(jwtUtil.generateToken(anyString(), any())).thenReturn("token");

        User savedUser = User.builder()
                .id(1L)
                .name(req.name())
                .email(req.email())
                .password("hashed")
                .role(Role.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse res = authService.register(req);

        assertNotNull(res.token());
        assertEquals("test@mail.com", res.email());
        assertEquals(Role.USER, res.role());
    }

    @Test
    void register_shouldThrowIfEmailExists() {
        RegisterRequest req = new RegisterRequest("Nishant", "test@mail.com", "password");

        when(userRepository.existsByEmail(req.email())).thenReturn(true);
        when(exceptionUtil.duplicate(anyString())).thenThrow(new RuntimeException("duplicate"));

        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void login_shouldThrowIfPasswordInvalid() {
        LoginRequest req = new LoginRequest("test@mail.com", "wrong");

        User user = User.builder()
                .email(req.email())
                .password("hashed")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(req.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.password(), user.getPassword())).thenReturn(false);
        when(exceptionUtil.unauthorized(anyString())).thenThrow(new RuntimeException("unauthorized"));

        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    @Test
    void login_shouldThrowIfUserNotFound() {
        LoginRequest req = new LoginRequest("test@mail.com", "password");

        when(userRepository.findByEmail(req.email())).thenReturn(Optional.empty());
        when(exceptionUtil.unauthorized(anyString())).thenThrow(new RuntimeException("unauthorized"));

        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    @Test
    void promoteUser_shouldUpgradeRoleToAdmin() {
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@mail.com")
                .role(Role.USER)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        PromoteUserResponse res = authService.promoteUserToAdmin(1L);

        assertEquals(Role.ADMIN, res.role());
        verify(userRepository).save(user);
    }

    @Test
    void promoteUser_shouldFailIfAlreadyAdmin() {
        User user = User.builder()
                .id(1L)
                .role(Role.ADMIN)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(exceptionUtil.badRequest(anyString())).thenThrow(new RuntimeException("bad request"));

        assertThrows(RuntimeException.class, () -> authService.promoteUserToAdmin(1L));
    }
}