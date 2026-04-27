package com.squadgo.squadgo_backend.review.repository;

import com.squadgo.squadgo_backend.review.entity.DestinationReview;
import com.squadgo.squadgo_backend.review.entity.ReviewType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DestinationReviewRepository extends JpaRepository<DestinationReview, UUID> {

    List<DestinationReview> findByDestinationIdOrderByCreatedAtDesc(UUID destinationId);

    Page<DestinationReview> findByDestinationId(UUID destinationId, Pageable pageable);

    List<DestinationReview> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);

    Page<DestinationReview> findByDestinationIdAndType(UUID destinationId, ReviewType type, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM DestinationReview r WHERE r.destination.id = :destId")
    Double findAverageRatingByDestinationId(@Param("destId") UUID destinationId);

    boolean existsByAuthorIdAndDestinationId(UUID authorId, UUID destinationId);
}
