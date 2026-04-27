package com.squadgo.squadgo_backend.match.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class SendMatchRequest {

    @NotNull(message = "Receiver ID is required")
    private UUID receiverId;

    @NotNull(message = "Destination ID is required")
    private UUID destinationId;

    @Size(max = 200, message = "Intro message must not exceed 200 characters")
    private String message;
}
