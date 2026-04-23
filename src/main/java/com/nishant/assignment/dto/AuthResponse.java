package com.nishant.assignment.dto;

import com.nishant.assignment.entity.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        Long id,
        String name,
        String email,
        Role role
) {
}
