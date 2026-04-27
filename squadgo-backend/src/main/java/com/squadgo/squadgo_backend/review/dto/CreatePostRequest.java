package com.squadgo.squadgo_backend.review.dto;

import com.squadgo.squadgo_backend.review.entity.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreatePostRequest {

    private UUID destinationId;

    @NotBlank(message = "Caption is required")
    private String caption;

    private List<String> mediaUrls;

    @NotNull(message = "Post type is required")
    private PostType postType;
}
