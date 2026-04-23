package com.nishant.assignment.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class SwaggerConfig {

    private static final String API_DESCRIPTION = """
            Backend Intern Assignment - Task Management API with JWT Authentication and Role-Based Access Control.\s
           \s
            Authentication:
            - Use Bearer JWT in the Authorization header: 'Authorization: Bearer <token>'.
            - Obtain token from register/login endpoints before accessing protected routes.
           \s
            Role behavior:
            - ADMIN users can view and manage all tasks, and can promote USER accounts to ADMIN.
            - USER accounts can only access and manage their own tasks.
           \s
            Features:
            - Authentication (register, login, current-user profile).
            - Task CRUD with ownership and role-based authorization.
            - Standardized error responses across the API.""";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Assignment API")
                        .version("v1")
                        .description(API_DESCRIPTION))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}