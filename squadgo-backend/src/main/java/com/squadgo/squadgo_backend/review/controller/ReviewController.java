package com.squadgo.squadgo_backend.review.controller;

import com.squadgo.squadgo_backend.common.dto.ApiResponse;
import com.squadgo.squadgo_backend.review.dto.*;
import com.squadgo.squadgo_backend.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ─────────────────────────────────────────────────────────────────────────
    // Review endpoints
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/api/v1/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request) {

        ReviewResponse response = reviewService.createReview(userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", response));
    }

    @GetMapping("/api/v1/reviews/destination/{destinationId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getDestinationReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ReviewResponse> reviews = reviewService.getDestinationReviews(destinationId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched successfully", reviews));
    }

    @GetMapping("/api/v1/reviews/destination/{destinationId}/tips")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getDestinationTips(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ReviewResponse> tips = reviewService.getDestinationTips(destinationId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Tips fetched successfully", tips));
    }

    @GetMapping("/api/v1/reviews/my")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<ReviewResponse> reviews = reviewService.getMyReviews(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Your reviews fetched successfully", reviews));
    }

    @PutMapping("/api/v1/reviews/{reviewId}/helpful")
    public ResponseEntity<ApiResponse<Void>> markHelpful(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID reviewId) {

        reviewService.markHelpful(userDetails.getUsername(), reviewId);
        return ResponseEntity.ok(ApiResponse.success("Marked as helpful", null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Feed / Community post endpoints
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/api/v1/feed")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePostRequest request) {

        PostResponse response = reviewService.createPost(userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Post created successfully", response));
    }

    @GetMapping("/api/v1/feed")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getFeed(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<PostResponse> feed = reviewService.getFeed(page, size);
        return ResponseEntity.ok(ApiResponse.success("Feed fetched successfully", feed));
    }

    @GetMapping("/api/v1/feed/destination/{destinationId}")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getDestinationPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<PostResponse> posts = reviewService.getDestinationPosts(destinationId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Destination posts fetched successfully", posts));
    }

    @GetMapping("/api/v1/feed/my")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getMyPosts(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<PostResponse> posts = reviewService.getMyPosts(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Your posts fetched successfully", posts));
    }

    @DeleteMapping("/api/v1/feed/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID postId) {

        reviewService.deletePost(userDetails.getUsername(), postId);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully", null));
    }
}
