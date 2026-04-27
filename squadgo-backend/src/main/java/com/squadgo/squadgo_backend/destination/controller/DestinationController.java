package com.squadgo.squadgo_backend.destination.controller;

import com.squadgo.squadgo_backend.common.dto.ApiResponse;
import com.squadgo.squadgo_backend.destination.dto.CreateDestinationRequest;
import com.squadgo.squadgo_backend.destination.dto.DestinationDetailResponse;
import com.squadgo.squadgo_backend.destination.dto.DestinationSummaryResponse;
import com.squadgo.squadgo_backend.destination.service.DestinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Destination resources.
 *
 * PUBLIC endpoints (no token required — add these to SecurityConfig.permitAll if needed):
 *   GET /api/v1/destinations
 *   GET /api/v1/destinations/search
 *
 * AUTHENTICATED endpoints (valid JWT required):
 *   GET /api/v1/destinations/{slug}
 *   GET /api/v1/destinations/category/{category}
 *   GET /api/v1/destinations/state/{state}
 *
 * ADMIN-ONLY endpoints (@PreAuthorize enforced):
 *   POST   /api/v1/destinations
 *   PUT    /api/v1/destinations/{id}
 *   DELETE /api/v1/destinations/{id}
 *
 * NOTE: To make the public GET endpoints truly unauthenticated, add the following
 * patterns to SecurityConfig's permitAll block:
 *   "/api/v1/destinations"
 *   "/api/v1/destinations/search"
 */
@RestController
@RequestMapping("/api/v1/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    // -------------------------------------------------------------------------
    // Public — list & search
    // -------------------------------------------------------------------------

    /**
     * GET /api/v1/destinations?page=0&size=10
     * Returns a paginated list of all active destinations (summary view).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DestinationSummaryResponse>>> getAllDestinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DestinationSummaryResponse> result = destinationService.getAllDestinations(page, size);
        return ResponseEntity.ok(ApiResponse.success("Destinations fetched successfully", result));
    }

    /**
     * GET /api/v1/destinations/search?q=manali&page=0&size=10
     * Search destinations by name query (case-insensitive, paginated).
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<DestinationSummaryResponse>>> searchDestinations(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DestinationSummaryResponse> result = destinationService.searchDestinations(query, page, size);
        return ResponseEntity.ok(ApiResponse.success("Search results fetched successfully", result));
    }

    // -------------------------------------------------------------------------
    // Authenticated — reads
    // -------------------------------------------------------------------------

    /**
     * GET /api/v1/destinations/{slug}
     * Returns full details for a destination identified by its slug.
     * Requires a valid JWT.
     */
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<DestinationDetailResponse>> getDestinationBySlug(
            @PathVariable String slug) {

        DestinationDetailResponse response = destinationService.getDestinationBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Destination details fetched successfully", response));
    }

    /**
     * GET /api/v1/destinations/category/{category}
     * Returns all active destinations in a given category.
     * Requires a valid JWT.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<DestinationSummaryResponse>>> getDestinationsByCategory(
            @PathVariable String category) {

        List<DestinationSummaryResponse> result = destinationService.getDestinationsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Destinations by category fetched successfully", result));
    }

    /**
     * GET /api/v1/destinations/state/{state}
     * Returns all active destinations in a given state (case-insensitive).
     * Requires a valid JWT.
     */
    @GetMapping("/state/{state}")
    public ResponseEntity<ApiResponse<List<DestinationSummaryResponse>>> getDestinationsByState(
            @PathVariable String state) {

        List<DestinationSummaryResponse> result = destinationService.getDestinationsByState(state);
        return ResponseEntity.ok(ApiResponse.success("Destinations by state fetched successfully", result));
    }

    // -------------------------------------------------------------------------
    // Admin — write operations
    // -------------------------------------------------------------------------

    /**
     * POST /api/v1/destinations
     * Creates a new destination. Admin only.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DestinationDetailResponse>> createDestination(
            @Valid @RequestBody CreateDestinationRequest request) {

        DestinationDetailResponse response = destinationService.createDestination(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Destination created successfully", response));
    }

    /**
     * PUT /api/v1/destinations/{id}
     * Updates an existing destination by ID. Admin only.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DestinationDetailResponse>> updateDestination(
            @PathVariable UUID id,
            @Valid @RequestBody CreateDestinationRequest request) {

        DestinationDetailResponse response = destinationService.updateDestination(id, request);
        return ResponseEntity.ok(ApiResponse.success("Destination updated successfully", response));
    }

    /**
     * DELETE /api/v1/destinations/{id}
     * Soft-deletes a destination (sets isActive=false). Admin only.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDestination(@PathVariable UUID id) {
        destinationService.deleteDestination(id);
        return ResponseEntity.ok(ApiResponse.success("Destination deleted successfully", null));
    }
}
