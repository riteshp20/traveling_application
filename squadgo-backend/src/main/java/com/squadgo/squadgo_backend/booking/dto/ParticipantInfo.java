package com.squadgo.squadgo_backend.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantInfo {

    private UUID userId;
    private String userName;
    private Integer shareAmount;
    private Boolean hasPaid;
}
