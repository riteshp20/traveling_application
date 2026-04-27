package com.squadgo.squadgo_backend.profile.controller;

import com.squadgo.squadgo_backend.common.dto.ApiResponse;
import com.squadgo.squadgo_backend.profile.dto.*;
import com.squadgo.squadgo_backend.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * GET /api/v1/profile
     * Returns the authenticated user's profile.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        ProfileResponse profile = profileService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    /**
     * PUT /api/v1/profile
     * Updates the authenticated user's profile fields.
     */
    @PutMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        ProfileResponse updated = profileService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    /**
     * POST /api/v1/profile/travel-history
     * Adds a new travel history entry for the authenticated user.
     */
    @PostMapping("/travel-history")
    public ResponseEntity<ApiResponse<TravelHistoryResponse>> addTravelHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TravelHistoryRequest request) {

        TravelHistoryResponse response = profileService.addTravelHistory(userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Travel history added successfully", response));
    }

    /**
     * GET /api/v1/profile/travel-history
     * Returns all travel history entries for the authenticated user.
     */
    @GetMapping("/travel-history")
    public ResponseEntity<ApiResponse<List<TravelHistoryResponse>>> getTravelHistory(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<TravelHistoryResponse> history = profileService.getTravelHistory(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Travel history retrieved successfully", history));
    }

    /**
     * DELETE /api/v1/profile/travel-history/{historyId}
     * Deletes a travel history entry. Only the owning user may delete their own entry.
     */
    @DeleteMapping("/travel-history/{historyId}")
    public ResponseEntity<ApiResponse<Void>> deleteTravelHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID historyId) {

        profileService.deleteTravelHistory(userDetails.getUsername(), historyId);
        return ResponseEntity.ok(ApiResponse.success("Travel history entry deleted successfully", null));
    }
}
