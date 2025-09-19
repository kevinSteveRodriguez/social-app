package com.redsocial.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePostRequest {

    @NotBlank
    @Size(max = 20000)
    private String content;

    @Size(max = 500)
    private String mediaUrl;

    public String getContent() {
        return content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }
}
