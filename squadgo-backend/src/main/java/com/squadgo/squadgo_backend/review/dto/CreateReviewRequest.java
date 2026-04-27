package com.squadgo.squadgo_backend.review.dto;

import com.squadgo.squadgo_backend.review.entity.ReviewType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateReviewRequest {

    @NotNull(message = "Destination ID is required")
    private UUID destinationId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    private List<String> photoUrls;

    @NotNull(message = "Review type is required")
    private ReviewType type;
}
