package com.illusion.ticketblitz.authservice.dto;

// This perfectly matches the root of your Angular interface
public record AuthResponse(
        String token,
        UserDto user
) {}