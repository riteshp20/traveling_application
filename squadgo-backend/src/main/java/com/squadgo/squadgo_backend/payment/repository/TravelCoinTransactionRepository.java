package com.squadgo.squadgo_backend.payment.repository;

import com.squadgo.squadgo_backend.payment.entity.TravelCoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TravelCoinTransactionRepository extends JpaRepository<TravelCoinTransaction, UUID> {

    List<TravelCoinTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
