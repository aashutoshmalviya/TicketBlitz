package com.illusion.ticketblitz.authservice.config;

import com.illusion.ticketblitz.authservice.entity.User;
import com.illusion.ticketblitz.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner initDatabase(UserRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.findByUsername("ashutosh").isEmpty()) {
                repository.save(User.builder()
                        .username("ashutosh")
                        .password(encoder.encode("admin123"))
                        .role("ROLE_ADMIN")
                        .build());
                // Dev convenience: seeded admin user for testing. Remove in production.
            }
        };
    }
}