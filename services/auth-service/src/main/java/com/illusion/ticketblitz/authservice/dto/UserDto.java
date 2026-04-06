package com.illusion.ticketblitz.authservice.dto;

import java.util.UUID;


public record UserDto(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName
) {}