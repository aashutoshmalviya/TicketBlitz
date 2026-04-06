package com.illusion.ticketblitz.authservice.config;

import com.illusion.ticketblitz.authservice.entity.User;
import com.illusion.ticketblitz.authservice.entity.UserRole;
import com.illusion.ticketblitz.authservice.entity.UserStatus;
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

            // 1. Seed the Admin User
            if (repository.findByEmail("ashutosh@ticketblitz.com").isEmpty()) {
                repository.save(User.builder()
                        .firstName("Ashutosh")
                        .lastName("Malviya")
                        .email("ashutosh@ticketblitz.com")
                        .password(encoder.encode("admin123"))
                        .role(UserRole.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .build());

                System.out.println("Seeded Admin user: ashutosh@ticketblitz.com");
            }

            // 2. Seed the Normal Customer User
            if (repository.findByEmail("customer@ticketblitz.com").isEmpty()) {
                repository.save(User.builder()
                        .firstName("Test")
                        .lastName("Customer")
                        .email("customer@ticketblitz.com")
                        .password(encoder.encode("user123"))
                        .role(UserRole.CUSTOMER)
                        .status(UserStatus.ACTIVE)
                        .build());

                System.out.println("Seeded Normal user: customer@ticketblitz.com");
            }

            System.out.println("Dev convenience: Seeded default users. Remove in production.");
        };
    }
}