package com.squadgo.squadgo_backend.match.controller;

import com.squadgo.squadgo_backend.common.dto.ApiResponse;
import com.squadgo.squadgo_backend.match.dto.MatchResponse;
import com.squadgo.squadgo_backend.match.dto.NearbyTravelerResponse;
import com.squadgo.squadgo_backend.match.dto.SendMatchRequest;
import com.squadgo.squadgo_backend.match.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<NearbyTravelerResponse>>> findNearbyTravelers(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<NearbyTravelerResponse> travelers = matchService.findNearbyTravelers(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Nearby travelers retrieved", travelers));
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<MatchResponse>> sendMatchRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SendMatchRequest request
    ) {
        MatchResponse response = matchService.sendMatchRequest(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Match request sent successfully", response));
    }

    @PutMapping("/{matchId}/accept")
    public ResponseEntity<ApiResponse<MatchResponse>> acceptMatchRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID matchId
    ) {
        MatchResponse response = matchService.respondToMatchRequest(userDetails.getUsername(), matchId, true);
        return ResponseEntity.ok(ApiResponse.success("Match request accepted", response));
    }

    @PutMapping("/{matchId}/decline")
    public ResponseEntity<ApiResponse<MatchResponse>> declineMatchRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID matchId
    ) {
        MatchResponse response = matchService.respondToMatchRequest(userDetails.getUsername(), matchId, false);
        return ResponseEntity.ok(ApiResponse.success("Match request declined", response));
    }

    @GetMapping("/requests/incoming")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getIncomingRequests(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<MatchResponse> requests = matchService.getIncomingRequests(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Incoming match requests retrieved", requests));
    }

    @GetMapping("/connections")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMyConnections(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<MatchResponse> connections = matchService.getMyConnections(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Connections retrieved", connections));
    }
}
