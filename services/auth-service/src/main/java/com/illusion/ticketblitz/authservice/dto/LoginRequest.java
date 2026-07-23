package com.illusion.ticketblitz.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Username/Email is required") String username,
        @NotBlank(message = "Password is required") String password
) {}