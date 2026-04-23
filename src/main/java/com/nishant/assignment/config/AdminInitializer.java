package com.nishant.assignment.config;

import com.nishant.assignment.entity.Role;
import com.nishant.assignment.entity.User;
import com.nishant.assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    @Value("${admin.name:Admin}")
    private String ADMIN_NAME;

    @Value("${admin.email:admin@example.com}")
    private String ADMIN_EMAIL;

    @Value("${admin.password:12345678}")
    private String ADMIN_PASSWORD;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull ... args) {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            log.info("Default admin already exists. Skipping initialization.");
            return;
        }

        User admin = User.builder()
                .name(ADMIN_NAME)
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Default admin user created with email: {}", ADMIN_EMAIL);
    }
}

