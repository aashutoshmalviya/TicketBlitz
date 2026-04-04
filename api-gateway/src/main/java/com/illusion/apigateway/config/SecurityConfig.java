package com.illusion.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // 1. Let users hit the Auth Service without a token
                        .pathMatchers("/api/auth/**").permitAll()

                        .pathMatchers("/api/catalog/**").permitAll()
                        // 2. Require a valid JWT for the Booking Service
                        .pathMatchers("/api/tickets/**").authenticated()

                        // 3. Deny everything else by default
                        .anyExchange().authenticated()
                )

                // 4. Tell Spring to validate tokens using the public key from the YAML
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}