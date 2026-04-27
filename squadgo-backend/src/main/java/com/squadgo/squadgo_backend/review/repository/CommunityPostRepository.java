package com.squadgo.squadgo_backend.review.repository;

import com.squadgo.squadgo_backend.review.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, UUID> {

    Page<CommunityPost> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<CommunityPost> findByDestinationIdAndIsActiveTrueOrderByCreatedAtDesc(UUID destinationId, Pageable pageable);

    List<CommunityPost> findByAuthorIdAndIsActiveTrueOrderByCreatedAtDesc(UUID authorId);
}
