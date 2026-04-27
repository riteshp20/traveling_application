package com.squadgo.squadgo_backend.payment.dto;

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
public class TravelCoinTransactionResponse {

    private UUID id;
    private Integer coins;
    private String reason;
    private LocalDateTime createdAt;
}
