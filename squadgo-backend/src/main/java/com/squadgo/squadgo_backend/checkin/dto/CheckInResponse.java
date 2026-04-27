package com.squadgo.squadgo_backend.checkin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CheckInResponse {

    private UUID checkInId;
    private UUID userId;
    private String userFullName;
    private UUID destinationId;
    private String destinationName;
    private String destinationCity;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;
}
