package com.squadgo.squadgo_backend.payment.dto;

import com.squadgo.squadgo_backend.payment.entity.PaymentPurpose;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitiatePaymentRequest {

    @NotNull
    private Integer amount;

    @NotNull
    private PaymentPurpose purpose;

    private UUID bookingId;

    private String description;
}
