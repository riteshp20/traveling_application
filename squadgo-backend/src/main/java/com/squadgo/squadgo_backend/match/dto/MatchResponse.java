package com.squadgo.squadgo_backend.match.dto;

import com.squadgo.squadgo_backend.match.entity.MatchStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MatchResponse {

    private UUID matchId;

    private UUID requesterId;
    private String requesterName;
    private String requesterPhotoUrl;

    private UUID receiverId;
    private String receiverName;
    private String receiverPhotoUrl;

    private UUID destinationId;
    private String destinationName;

    private MatchStatus status;
    private String message;
    private LocalDateTime createdAt;
}
