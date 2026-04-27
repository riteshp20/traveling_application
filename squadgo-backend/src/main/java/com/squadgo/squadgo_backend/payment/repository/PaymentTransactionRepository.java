package com.squadgo.squadgo_backend.payment.repository;

import com.squadgo.squadgo_backend.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    List<PaymentTransaction> findByPayerIdOrderByCreatedAtDesc(UUID payerId);

    Optional<PaymentTransaction> findByRazorpayOrderId(String razorpayOrderId);
}
