package com.squadgo.squadgo_backend.review.dto;

import com.squadgo.squadgo_backend.review.entity.ReviewType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReviewResponse {

    private UUID reviewId;
    private UUID authorId;
    private String authorName;
    private String authorPhotoUrl;
    private Boolean authorVerified;

    private UUID destinationId;
    private String destinationName;

    private Integer rating;
    private String title;
    private String body;
    private List<String> photoUrls;

    private ReviewType type;
    private Integer helpfulVotes;
    private LocalDateTime createdAt;
}
