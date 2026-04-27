package com.squadgo.squadgo_backend.checkin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CheckInRequest {

    @NotNull(message = "Destination ID is required")
    private UUID destinationId;

    @NotNull(message = "User latitude is required")
    private Double userLatitude;

    @NotNull(message = "User longitude is required")
    private Double userLongitude;
}
