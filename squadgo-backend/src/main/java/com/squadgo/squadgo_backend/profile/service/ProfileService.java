package com.squadgo.squadgo_backend.profile.service;

import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.auth.repository.UserRepository;
import com.squadgo.squadgo_backend.profile.dto.*;
import com.squadgo.squadgo_backend.profile.entity.TravelHistory;
import com.squadgo.squadgo_backend.profile.repository.TravelHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final TravelHistoryRepository travelHistoryRepository;

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Converts a comma-separated interests string stored in the DB into a List<String>.
     * Returns an empty list if the raw value is null or blank.
     */
    private List<String> parseInterests(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Converts a List<String> of interests into a comma-separated string for DB storage.
     * Returns null if the list is null or empty.
     */
    private String serializeInterests(List<String> interests) {
        if (interests == null || interests.isEmpty()) {
            return null;
        }
        return interests.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }

    private ProfileResponse toProfileResponse(User user) {
        return ProfileResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .bio(user.getBio())
                .interests(parseInterests(user.getInterests()))
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .isVerified(user.getIsVerified())
                .travelCoins(user.getTravelCoins())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private TravelHistoryResponse toTravelHistoryResponse(TravelHistory history) {
        return TravelHistoryResponse.builder()
                .id(history.getId())
                .destination(history.getDestination())
                .city(history.getCity())
                .state(history.getState())
                .country(history.getCountry())
                .visitedOn(history.getVisitedOn())
                .notes(history.getNotes())
                .build();
    }

    // ─── Service Methods ─────────────────────────────────────────────────────────

    /**
     * Returns the profile for the authenticated user.
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        User user = findUserByEmail(email);
        return toProfileResponse(user);
    }

    /**
     * Updates mutable profile fields for the authenticated user.
     * Phone uniqueness is enforced when the phone number is changed.
     */
    @Transactional
    public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findUserByEmail(email);

        // Phone uniqueness check — only validate when the value actually changes
        String newPhone = request.getPhone();
        if (newPhone != null && !newPhone.isBlank()) {
            boolean phoneChanged = !newPhone.equals(user.getPhone());
            if (phoneChanged && userRepository.existsByPhone(newPhone)) {
                throw new RuntimeException("Phone number " + newPhone + " is already in use by another account");
            }
            user.setPhone(newPhone);
        }

        user.setFullName(request.getFullName());
        user.setBio(request.getBio());
        user.setProfilePhotoUrl(request.getProfilePhotoUrl());
        user.setInterests(serializeInterests(request.getInterests()));

        User saved = userRepository.save(user);
        return toProfileResponse(saved);
    }

    /**
     * Adds a new travel history entry for the authenticated user.
     */
    @Transactional
    public TravelHistoryResponse addTravelHistory(String email, TravelHistoryRequest request) {
        User user = findUserByEmail(email);

        String country = (request.getCountry() != null && !request.getCountry().isBlank())
                ? request.getCountry()
                : "India";

        TravelHistory history = TravelHistory.builder()
                .user(user)
                .destination(request.getDestination())
                .city(request.getCity())
                .state(request.getState())
                .country(country)
                .visitedOn(request.getVisitedOn())
                .notes(request.getNotes())
                .build();

        TravelHistory saved = travelHistoryRepository.save(history);
        return toTravelHistoryResponse(saved);
    }

    /**
     * Returns all travel history entries for the authenticated user, ordered by visit date descending.
     */
    @Transactional(readOnly = true)
    public List<TravelHistoryResponse> getTravelHistory(String email) {
        User user = findUserByEmail(email);
        return travelHistoryRepository.findByUserIdOrderByVisitedOnDesc(user.getId())
                .stream()
                .map(this::toTravelHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a travel history entry. Only the owning user may delete their own entries.
     */
    @Transactional
    public void deleteTravelHistory(String email, UUID historyId) {
        User user = findUserByEmail(email);

        TravelHistory history = travelHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("Travel history entry not found with id: " + historyId));

        if (!history.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this travel history entry");
        }

        travelHistoryRepository.delete(history);
    }
}
