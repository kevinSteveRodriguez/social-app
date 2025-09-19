package com.redsocial.app.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PostResponse {
    private UUID id;
    private UUID userId;
    private String content;
    private String mediaUrl;
    private int likesCount;
    private int commentsCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String alias;

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

    public PostResponse() {
        
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getContent() {
        return content;
    }
    public void setContent(String content) { this.content = content; }

    public String getMediaUrl() {
        return mediaUrl;
    }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public Integer getLikesCount() {
        return likesCount;
    }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }

    public Integer getCommentsCount() {
        return commentsCount;
    }
    public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
}
