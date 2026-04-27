package com.squadgo.squadgo_backend.destination.repository;

import com.squadgo.squadgo_backend.destination.entity.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {

    Optional<Destination> findBySlug(String slug);

    List<Destination> findByIsActiveTrue();

    List<Destination> findByCategoryAndIsActiveTrue(String category);

    List<Destination> findByStateIgnoreCaseAndIsActiveTrue(String state);

    List<Destination> findByIsPremiumAndIsActiveTrue(Boolean isPremium);

    Page<Destination> findByIsActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
}
