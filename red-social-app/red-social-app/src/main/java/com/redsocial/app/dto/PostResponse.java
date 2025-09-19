package com.redsocial.app.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PostResponse {
    private UUID id;
    private UUID userId;
    private String content;
    private String mediaUrl;
    private Integer likesCount;
    private Integer commentsCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public PostResponse(UUID id, UUID userId, String content, String mediaUrl, Integer likesCount, Integer commentsCount, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
