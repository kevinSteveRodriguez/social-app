package com.redsocial.app.controller;

import com.redsocial.app.dto.AuthRequest;
import com.redsocial.app.dto.AuthResponse;
import com.redsocial.app.dto.RegisterRequest;
import com.redsocial.app.model.User;
import com.redsocial.app.repository.UserRepository;
import com.redsocial.app.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    request.getEmail().toLowerCase().trim(),
                    request.getPassword()
            );
            authenticationManager.authenticate(authentication);
            var user = userRepository.findByEmail(request.getEmail().toLowerCase().trim()).orElseThrow();
            if (!user.isEnabled()) {
                throw new BadCredentialsException("Usuario inactivo");
            }
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(409).build();
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setVerified(false);
        userRepository.save(user);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal(expression = "username") String username) {
        if (username == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(username);
    }
}
