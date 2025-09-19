package com.redsocial.app.service;

import com.redsocial.app.dto.CreatePostRequest;
import com.redsocial.app.dto.PostResponse;
import com.redsocial.app.exception.PostException;
import com.redsocial.app.exception.ValidationException;
import com.redsocial.app.model.Post;
import com.redsocial.app.model.User;
import com.redsocial.app.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private static final int MAX_CONTENT_LENGTH = 1000;
    private static final int MAX_MEDIA_URL_LENGTH = 500;

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse create(User user, CreatePostRequest request) {
        logger.debug("Creando post para usuario: {}", user.getId());
        
        try {
            // Validaciones
            validateCreatePostRequest(request);
            validateUser(user);
            
            Post post = new Post();
            post.setUser(user);
            post.setContent(request.getContent());
            post.setMediaUrl(request.getMediaUrl());
            post.setLikesCount(0);
            post.setCommentsCount(0);
            
            Post saved = postRepository.save(post);
            logger.info("Post creado exitosamente con ID: {}", saved.getId());
            
            return toResponse(saved);
            
        } catch (ValidationException | PostException e) {
            logger.warn("Error al crear post para usuario {}: {}", user.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al crear post para usuario {}: ", user.getId(), e);
            throw new PostException("Error interno al crear el post", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> listAll(Pageable pageable) {
        logger.debug("Listando todos los posts con paginación: {}", pageable);
    
        Page<Post> page = postRepository.findAllWithUserProfile(pageable);
        return page.map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<PostResponse> listByUser(UUID userId, Pageable pageable) {
        logger.debug("Listando posts para usuario: {} con paginación: {}", userId, pageable);
    
        Page<Post> page = postRepository.findByUserIdWithUserProfile(userId, pageable);
        return page.map(this::toResponse);
    }

    private PostResponse toResponse(Post post) {
        PostResponse resp = new PostResponse();
        resp.setId(post.getId());
        resp.setUserId(post.getUser().getId());
        resp.setContent(post.getContent());
        resp.setMediaUrl(post.getMediaUrl());
        resp.setLikesCount(post.getLikesCount());
        resp.setCommentsCount(post.getCommentsCount());
        resp.setCreatedAt(post.getCreatedAt());
        resp.setUpdatedAt(post.getUpdatedAt());
        // Añadir alias de perfil
        if (post.getUser() != null && post.getUser().getProfile() != null) {
            resp.setAlias(post.getUser().getProfile().getAlias());
        }
        return resp;
    }
    /**
     * Valida el request de creación de post.
     */
    private void validateCreatePostRequest(CreatePostRequest request) {
        if (request == null) {
            throw new ValidationException("El request no puede ser nulo");
        }
        
        if (!StringUtils.hasText(request.getContent()) && !StringUtils.hasText(request.getMediaUrl())) {
            throw new ValidationException("El post debe tener contenido o media URL");
        }
        
        if (StringUtils.hasText(request.getContent()) && request.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new ValidationException("El contenido del post no puede exceder " + MAX_CONTENT_LENGTH + " caracteres");
        }
        
        if (StringUtils.hasText(request.getMediaUrl()) && request.getMediaUrl().length() > MAX_MEDIA_URL_LENGTH) {
            throw new ValidationException("La URL del media no puede exceder " + MAX_MEDIA_URL_LENGTH + " caracteres");
        }
    }

    /**
     * Valida que el usuario no sea nulo.
     */
    private void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("El usuario no puede ser nulo");
        }
        
        if (user.getId() == null) {
            throw new ValidationException("El ID del usuario no puede ser nulo");
        }
    }

    /**
     * Valida que el ID del usuario no sea nulo.
     */
    private void validateUserId(UUID userId) {
        if (userId == null) {
            throw new ValidationException("El ID del usuario no puede ser nulo");
        }
    }

    /**
     * Valida que el objeto Pageable no sea nulo.
     */
    private void validatePageable(Pageable pageable) {
        if (pageable == null) {
            throw new ValidationException("El objeto Pageable no puede ser nulo");
        }
    }
}
