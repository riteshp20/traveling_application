package com.squadgo.squadgo_backend.review.service;

import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.destination.entity.Destination;
import com.squadgo.squadgo_backend.destination.repository.DestinationRepository;
import com.squadgo.squadgo_backend.review.dto.*;
import com.squadgo.squadgo_backend.review.entity.CommunityPost;
import com.squadgo.squadgo_backend.review.entity.DestinationReview;
import com.squadgo.squadgo_backend.review.entity.ReviewType;
import com.squadgo.squadgo_backend.review.repository.CommunityPostRepository;
import com.squadgo.squadgo_backend.review.repository.DestinationReviewRepository;
import com.squadgo.squadgo_backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final DestinationReviewRepository reviewRepository;
    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Review operations
    // ─────────────────────────────────────────────────────────────────────────

    public ReviewResponse createReview(String email, CreateReviewRequest request) {
        User author = findUserByEmail(email);
        Destination destination = findDestinationById(request.getDestinationId());

        if (reviewRepository.existsByAuthorIdAndDestinationId(author.getId(), destination.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You have already submitted a review for this destination");
        }

        DestinationReview review = DestinationReview.builder()
                .author(author)
                .destination(destination)
                .rating(request.getRating())
                .title(request.getTitle())
                .body(request.getBody())
                .photoUrls(joinUrls(request.getPhotoUrls()))
                .type(request.getType())
                .build();

        DestinationReview saved = reviewRepository.save(review);
        return toReviewResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getDestinationReviews(UUID destinationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Use the typed query — passing null type returns all types; we use a dedicated query for tips
        // For all reviews we use findAll with spec; here we reuse the findByDestinationIdAndType
        // across all types using Spring-derived query on destination only.
        // We call the list method and convert to Page manually OR use JPA directly.
        // Using findByDestinationIdAndType won't work for "all". We add a simple derived query.
        return reviewRepository
                .findByDestinationId(destinationId, pageable)
                .map(this::toReviewResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getDestinationTips(UUID destinationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository
                .findByDestinationIdAndType(destinationId, ReviewType.TIP, pageable)
                .map(this::toReviewResponse);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(String email) {
        User author = findUserByEmail(email);
        return reviewRepository
                .findByAuthorIdOrderByCreatedAtDesc(author.getId())
                .stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    public void markHelpful(String email, UUID reviewId) {
        // Verify the user exists (auth guard)
        findUserByEmail(email);

        DestinationReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        review.setHelpfulVotes(review.getHelpfulVotes() + 1);
        reviewRepository.save(review);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Community post operations
    // ─────────────────────────────────────────────────────────────────────────

    public PostResponse createPost(String email, CreatePostRequest request) {
        User author = findUserByEmail(email);

        Destination destination = null;
        if (request.getDestinationId() != null) {
            destination = findDestinationById(request.getDestinationId());
        }

        CommunityPost post = CommunityPost.builder()
                .author(author)
                .destination(destination)
                .caption(request.getCaption())
                .mediaUrls(joinUrls(request.getMediaUrls()))
                .postType(request.getPostType())
                .build();

        CommunityPost saved = postRepository.save(post);
        return toPostResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getFeed(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::toPostResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getDestinationPosts(UUID destinationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository
                .findByDestinationIdAndIsActiveTrueOrderByCreatedAtDesc(destinationId, pageable)
                .map(this::toPostResponse);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getMyPosts(String email) {
        User author = findUserByEmail(email);
        return postRepository
                .findByAuthorIdAndIsActiveTrueOrderByCreatedAtDesc(author.getId())
                .stream()
                .map(this::toPostResponse)
                .collect(Collectors.toList());
    }

    public void deletePost(String email, UUID postId) {
        User author = findUserByEmail(email);

        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not authorized to delete this post");
        }

        post.setIsActive(false);
        postRepository.save(post);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Destination findDestinationById(UUID id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found"));
    }

    private String joinUrls(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return null;
        }
        return String.join(",", urls);
    }

    private List<String> splitUrls(String urls) {
        if (urls == null || urls.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(urls.split(","));
    }

    private ReviewResponse toReviewResponse(DestinationReview review) {
        User author = review.getAuthor();
        Destination destination = review.getDestination();

        return ReviewResponse.builder()
                .reviewId(review.getId())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .authorPhotoUrl(author.getProfilePhotoUrl())
                .authorVerified(author.getIsVerified())
                .destinationId(destination.getId())
                .destinationName(destination.getName())
                .rating(review.getRating())
                .title(review.getTitle())
                .body(review.getBody())
                .photoUrls(splitUrls(review.getPhotoUrls()))
                .type(review.getType())
                .helpfulVotes(review.getHelpfulVotes())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private PostResponse toPostResponse(CommunityPost post) {
        User author = post.getAuthor();
        Destination destination = post.getDestination();

        return PostResponse.builder()
                .postId(post.getId())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .authorPhotoUrl(author.getProfilePhotoUrl())
                .authorVerified(author.getIsVerified())
                .destinationId(destination != null ? destination.getId() : null)
                .destinationName(destination != null ? destination.getName() : null)
                .caption(post.getCaption())
                .mediaUrls(splitUrls(post.getMediaUrls()))
                .postType(post.getPostType())
                .likesCount(post.getLikesCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
