package com.redsocial.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Deshabilita CSRF en desarrollo; si expones formularios, ajústalo
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                // Permite libremente actuator básico (ajusta a tus necesidades)
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // También conviene permitir los endpoints de auth
                .requestMatchers("/api/auth/**").permitAll()
                // Permite todo el resto (para desarrollo). En prod, cambia a .authenticated()
                .anyRequest().permitAll()
        );

        // No configures login; al permitir todo, no será necesario
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
