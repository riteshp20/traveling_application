package com.squadgo.squadgo_backend.checkin.repository;

import com.squadgo.squadgo_backend.checkin.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

    List<CheckIn> findByDestinationIdAndIsActiveTrue(UUID destinationId);

    Optional<CheckIn> findByUserIdAndIsActiveTrue(UUID userId);

    boolean existsByUserIdAndIsActiveTrue(UUID userId);

    List<CheckIn> findByUserIdOrderByCheckedInAtDesc(UUID userId);
}
