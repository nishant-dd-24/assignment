package com.nishant.assignment.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "System roles used for authorization")
public enum Role {
    @Schema(description = "Standard user with access limited to own tasks")
    USER,
    @Schema(description = "Administrator with access to all tasks and role-promotion endpoints")
    ADMIN
}