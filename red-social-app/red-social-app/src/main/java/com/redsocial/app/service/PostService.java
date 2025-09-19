package com.redsocial.app.service;

import com.redsocial.app.dto.CreatePostRequest;
import com.redsocial.app.dto.PostResponse;
import com.redsocial.app.model.Post;
import com.redsocial.app.model.User;
import com.redsocial.app.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse create(User user, CreatePostRequest request) {
        Post post = new Post();
        post.setUser(user);
        post.setContent(request.getContent());
        post.setMediaUrl(request.getMediaUrl());
        post.setLikesCount(0);
        post.setCommentsCount(0);
        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> listAll(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> listByUser(UUID userId, Pageable pageable) {
        return postRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable).map(this::toResponse);
    }

    private PostResponse toResponse(Post p) {
        return new PostResponse(
                p.getId(),
                p.getUser() != null ? p.getUser().getId() : null,
                p.getContent(),
                p.getMediaUrl(),
                p.getLikesCount(),
                p.getCommentsCount(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
