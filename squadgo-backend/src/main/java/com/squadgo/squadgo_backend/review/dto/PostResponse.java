package com.squadgo.squadgo_backend.review.dto;

import com.squadgo.squadgo_backend.review.entity.PostType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PostResponse {

    private UUID postId;
    private UUID authorId;
    private String authorName;
    private String authorPhotoUrl;
    private Boolean authorVerified;

    private UUID destinationId;
    private String destinationName;

    private String caption;
    private List<String> mediaUrls;

    private PostType postType;
    private Integer likesCount;
    private LocalDateTime createdAt;
}
