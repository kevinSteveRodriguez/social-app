package com.redsocial.app.controller;

import com.redsocial.app.dto.CreatePostRequest;
import com.redsocial.app.dto.PostResponse;
import com.redsocial.app.model.User;
import com.redsocial.app.repository.UserRepository;
import com.redsocial.app.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    public PostController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    // Crear publicación (requiere autenticación)
    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@AuthenticationPrincipal UserDetails principal,
                                   @RequestBody @Valid CreatePostRequest request) {
        if (principal == null) {
            throw new org.springframework.security.access.AccessDeniedException("No autenticado");
        }
        String email = principal.getUsername();
        User user = userRepository.findByEmail(email == null ? null : email.toLowerCase().trim())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuario no encontrado"));
        return postService.create(user, request);
    }

    // Listar publicaciones (paginado, orden desc por fecha)
    @GetMapping("/posts")
    public Page<PostResponse> listPosts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return postService.listAll(pageable);
    }

    // Listar publicaciones de un usuario específico (opcional, por si lo necesitas)
    @GetMapping("/users/{userId}/posts")
    public Page<PostResponse> listUserPosts(@PathVariable UUID userId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return postService.listByUser(userId, pageable);
    }
}
