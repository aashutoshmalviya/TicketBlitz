package com.illusion.ticketblitz.authservice.controller;

import com.illusion.ticketblitz.authservice.dto.AuthResponse;
import com.illusion.ticketblitz.authservice.dto.LoginRequest;
import com.illusion.ticketblitz.authservice.dto.UserDto;
import com.illusion.ticketblitz.authservice.security.CustomUserDetails;
import com.illusion.ticketblitz.authservice.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Triggers Spring Security's authentication flow with our custom UserDetailsService
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            String role = authentication.getAuthorities().iterator().next().getAuthority();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserDto userDto = new UserDto(
                    userDetails.getId(),
                    userDetails.getUsername(), // or a distinct username if your entity has one
                    userDetails.getUsername(), // assuming email is used for both right now
                    userDetails.getFirstName(),
                    userDetails.getLastName()
            );

            String token = jwtService.generateToken(authentication.getName(), role);

            return ResponseEntity.ok(new AuthResponse(token,userDto));

        } catch (AuthenticationException e) {
            // BadCredentialsException or similar - don't leak user existence
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}