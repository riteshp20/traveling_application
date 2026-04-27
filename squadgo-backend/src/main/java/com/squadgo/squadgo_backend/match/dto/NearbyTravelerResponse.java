package com.squadgo.squadgo_backend.match.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class NearbyTravelerResponse {

    private UUID userId;
    private String fullName;
    private String profilePhotoUrl;

    /**
     * Whether the traveler's account is verified.
     */
    private Boolean isVerified;

    /**
     * Parsed from the user's comma-separated interests string.
     */
    private List<String> interests;

    private UUID destinationId;
    private String destinationName;
    private LocalDateTime checkedInAt;

    /**
     * True if the current user has already sent a match request to this traveler.
     */
    private Boolean alreadyRequested;
}
