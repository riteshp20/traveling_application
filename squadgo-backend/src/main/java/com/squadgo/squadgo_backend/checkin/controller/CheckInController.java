package com.squadgo.squadgo_backend.checkin.controller;

import com.squadgo.squadgo_backend.checkin.dto.CheckInRequest;
import com.squadgo.squadgo_backend.checkin.dto.CheckInResponse;
import com.squadgo.squadgo_backend.checkin.service.CheckInService;
import com.squadgo.squadgo_backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkin")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheckInRequest request
    ) {
        CheckInResponse response = checkInService.checkIn(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Checked in successfully", response));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckInResponse>> checkOut(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CheckInResponse response = checkInService.checkOut(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Checked out successfully", response));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<CheckInResponse>> getMyCurrentCheckIn(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CheckInResponse response = checkInService.getMyCurrentCheckIn(userDetails.getUsername());
        if (response == null) {
            return ResponseEntity.ok(ApiResponse.success("No active check-in", null));
        }
        return ResponseEntity.ok(ApiResponse.success("Current check-in retrieved", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<CheckInResponse>>> getMyCheckInHistory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<CheckInResponse> history = checkInService.getMyCheckInHistory(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Check-in history retrieved", history));
    }

    @GetMapping("/destination/{destinationId}/active")
    public ResponseEntity<ApiResponse<List<CheckInResponse>>> getActiveCheckInsAtDestination(
            @PathVariable UUID destinationId
    ) {
        List<CheckInResponse> active = checkInService.getActiveCheckInsAtDestination(destinationId);
        return ResponseEntity.ok(ApiResponse.success("Active check-ins retrieved", active));
    }
}
