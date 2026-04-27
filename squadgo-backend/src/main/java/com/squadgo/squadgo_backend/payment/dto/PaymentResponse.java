package com.squadgo.squadgo_backend.payment.dto;

import com.squadgo.squadgo_backend.payment.entity.PaymentPurpose;
import com.squadgo.squadgo_backend.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private UUID transactionId;
    private String razorpayOrderId;
    private Integer amount;
    private String currency;
    private PaymentStatus status;
    private PaymentPurpose purpose;
    private String description;
    private LocalDateTime createdAt;
}
