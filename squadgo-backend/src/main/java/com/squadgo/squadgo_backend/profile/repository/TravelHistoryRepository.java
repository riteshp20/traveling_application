package com.squadgo.squadgo_backend.profile.repository;

import com.squadgo.squadgo_backend.profile.entity.TravelHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TravelHistoryRepository extends JpaRepository<TravelHistory, UUID> {

    List<TravelHistory> findByUserIdOrderByVisitedOnDesc(UUID userId);
}
