package com.squadgo.squadgo_backend.destination.service;

import com.squadgo.squadgo_backend.destination.dto.CreateDestinationRequest;
import com.squadgo.squadgo_backend.destination.dto.DestinationDetailResponse;
import com.squadgo.squadgo_backend.destination.dto.DestinationSummaryResponse;
import com.squadgo.squadgo_backend.destination.entity.Destination;
import com.squadgo.squadgo_backend.destination.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository destinationRepository;

    // -------------------------------------------------------------------------
    // Public / authenticated reads
    // -------------------------------------------------------------------------

    /**
     * Returns a paginated list of all active destinations (lightweight summary).
     */
    public Page<DestinationSummaryResponse> getAllDestinations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        // Use a dedicated pageable query for active destinations
        return destinationRepository
                .findByIsActiveTrueAndNameContainingIgnoreCase("", pageable)
                .map(this::toSummaryResponse);
    }

    /**
     * Full-text search on destination name (case-insensitive), paginated.
     */
    public Page<DestinationSummaryResponse> searchDestinations(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return destinationRepository
                .findByIsActiveTrueAndNameContainingIgnoreCase(query == null ? "" : query, pageable)
                .map(this::toSummaryResponse);
    }

    /**
     * Returns full destination details for a given slug. Throws 404 if not found
     * or the destination is inactive.
     */
    public DestinationDetailResponse getDestinationBySlug(String slug) {
        Destination destination = destinationRepository.findBySlug(slug)
                .filter(Destination::getIsActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Destination not found: " + slug));
        return toDetailResponse(destination);
    }

    /**
     * Returns all active destinations matching the given category.
     */
    public List<DestinationSummaryResponse> getDestinationsByCategory(String category) {
        return destinationRepository.findByCategoryAndIsActiveTrue(category.toUpperCase())
                .stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns all active destinations in the given state (case-insensitive).
     */
    public List<DestinationSummaryResponse> getDestinationsByState(String state) {
        return destinationRepository.findByStateIgnoreCaseAndIsActiveTrue(state)
                .stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Admin operations
    // -------------------------------------------------------------------------

    /**
     * Creates a new destination. Slug is auto-generated from name + state.
     * [Admin only — enforced at controller layer via @PreAuthorize]
     */
    @Transactional
    public DestinationDetailResponse createDestination(CreateDestinationRequest request) {
        String slug = generateSlug(request.getName(), request.getState());

        Destination destination = Destination.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry() != null ? request.getCountry() : "India")
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .category(request.getCategory().toUpperCase())
                .imageUrl(request.getImageUrl())
                .tags(listToCsv(request.getTags()))
                .minBudgetPerDay(request.getMinBudgetPerDay())
                .maxBudgetPerDay(request.getMaxBudgetPerDay())
                .nearestStation(request.getNearestStation())
                .stayOptions(listToCsv(request.getStayOptions()))
                .bestTimeToVisit(request.getBestTimeToVisit())
                .isPremium(request.getIsPremium() != null ? request.getIsPremium() : false)
                .isActive(true)
                .build();

        Destination saved = destinationRepository.save(destination);
        return toDetailResponse(saved);
    }

    /**
     * Updates an existing destination by ID. Slug is regenerated from new name + state.
     * [Admin only — enforced at controller layer via @PreAuthorize]
     */
    @Transactional
    public DestinationDetailResponse updateDestination(UUID id, CreateDestinationRequest request) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Destination not found with id: " + id));

        destination.setName(request.getName());
        destination.setSlug(generateSlug(request.getName(), request.getState()));
        destination.setDescription(request.getDescription());
        destination.setCity(request.getCity());
        destination.setState(request.getState());
        if (request.getCountry() != null) {
            destination.setCountry(request.getCountry());
        }
        destination.setLatitude(request.getLatitude());
        destination.setLongitude(request.getLongitude());
        destination.setCategory(request.getCategory().toUpperCase());
        destination.setImageUrl(request.getImageUrl());
        destination.setTags(listToCsv(request.getTags()));
        destination.setMinBudgetPerDay(request.getMinBudgetPerDay());
        destination.setMaxBudgetPerDay(request.getMaxBudgetPerDay());
        destination.setNearestStation(request.getNearestStation());
        destination.setStayOptions(listToCsv(request.getStayOptions()));
        destination.setBestTimeToVisit(request.getBestTimeToVisit());
        if (request.getIsPremium() != null) {
            destination.setIsPremium(request.getIsPremium());
        }

        Destination saved = destinationRepository.save(destination);
        return toDetailResponse(saved);
    }

    /**
     * Soft-deletes a destination by setting isActive = false.
     * [Admin only — enforced at controller layer via @PreAuthorize]
     */
    @Transactional
    public void deleteDestination(UUID id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Destination not found with id: " + id));
        destination.setIsActive(false);
        destinationRepository.save(destination);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Generates a URL-friendly slug:
     * e.g. "Manali" + "Himachal Pradesh" -> "manali-himachal-pradesh"
     */
    private String generateSlug(String name, String state) {
        String namePart = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
        String statePart = state.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
        return namePart + "-" + statePart;
    }

    /** Converts a List<String> to a comma-separated String; returns null for null/empty input. */
    private String listToCsv(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream()
                .map(String::trim)
                .collect(Collectors.joining(","));
    }

    /** Converts a comma-separated String to a List<String>; returns empty list for null/blank input. */
    private List<String> csvToList(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private DestinationSummaryResponse toSummaryResponse(Destination d) {
        return DestinationSummaryResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .slug(d.getSlug())
                .city(d.getCity())
                .state(d.getState())
                .category(d.getCategory())
                .imageUrl(d.getImageUrl())
                .minBudgetPerDay(d.getMinBudgetPerDay())
                .maxBudgetPerDay(d.getMaxBudgetPerDay())
                .isPremium(d.getIsPremium())
                .bestTimeToVisit(d.getBestTimeToVisit())
                .build();
    }

    private DestinationDetailResponse toDetailResponse(Destination d) {
        return DestinationDetailResponse.builder()
                // summary fields
                .id(d.getId())
                .name(d.getName())
                .slug(d.getSlug())
                .city(d.getCity())
                .state(d.getState())
                .category(d.getCategory())
                .imageUrl(d.getImageUrl())
                .minBudgetPerDay(d.getMinBudgetPerDay())
                .maxBudgetPerDay(d.getMaxBudgetPerDay())
                .isPremium(d.getIsPremium())
                .bestTimeToVisit(d.getBestTimeToVisit())
                // detail-only fields
                .description(d.getDescription())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .tags(csvToList(d.getTags()))
                .nearestStation(d.getNearestStation())
                .stayOptions(csvToList(d.getStayOptions()))
                .createdAt(d.getCreatedAt())
                .build();
    }
}
