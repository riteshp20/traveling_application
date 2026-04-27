package com.squadgo.squadgo_backend.checkin.service;

import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.auth.repository.UserRepository;
import com.squadgo.squadgo_backend.checkin.dto.CheckInRequest;
import com.squadgo.squadgo_backend.checkin.dto.CheckInResponse;
import com.squadgo.squadgo_backend.checkin.entity.CheckIn;
import com.squadgo.squadgo_backend.checkin.repository.CheckInRepository;
import com.squadgo.squadgo_backend.destination.entity.Destination;
import com.squadgo.squadgo_backend.destination.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    @Transactional
    public CheckInResponse checkIn(String email, CheckInRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent double check-in
        checkInRepository.findByUserIdAndIsActiveTrue(user.getId()).ifPresent(existing -> {
            String destinationName = existing.getDestination().getName();
            throw new RuntimeException(
                    "You are already checked in at " + destinationName + ". Check out first."
            );
        });

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        double distance = calculateDistanceKm(
                request.getUserLatitude(), request.getUserLongitude(),
                destination.getLatitude(), destination.getLongitude()
        );

        boolean isVerified = distance <= 5.0;

        CheckIn checkIn = CheckIn.builder()
                .user(user)
                .destination(destination)
                .userLatitude(request.getUserLatitude())
                .userLongitude(request.getUserLongitude())
                .isVerified(isVerified)
                .isActive(true)
                .build();

        CheckIn saved = checkInRepository.save(checkIn);
        return toResponse(saved);
    }

    @Transactional
    public CheckInResponse checkOut(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CheckIn checkIn = checkInRepository.findByUserIdAndIsActiveTrue(user.getId())
                .orElseThrow(() -> new RuntimeException("No active check-in found. You are not checked in anywhere."));

        checkIn.setIsActive(false);
        checkIn.setCheckedOutAt(LocalDateTime.now());

        CheckIn saved = checkInRepository.save(checkIn);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CheckInResponse> getActiveCheckInsAtDestination(UUID destinationId) {
        return checkInRepository.findByDestinationIdAndIsActiveTrue(destinationId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CheckInResponse> getMyCheckInHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return checkInRepository.findByUserIdOrderByCheckedInAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CheckInResponse getMyCurrentCheckIn(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return checkInRepository.findByUserIdAndIsActiveTrue(user.getId())
                .map(this::toResponse)
                .orElse(null);
    }

    // --- Helpers ---

    private CheckInResponse toResponse(CheckIn checkIn) {
        return CheckInResponse.builder()
                .checkInId(checkIn.getId())
                .userId(checkIn.getUser().getId())
                .userFullName(checkIn.getUser().getFullName())
                .destinationId(checkIn.getDestination().getId())
                .destinationName(checkIn.getDestination().getName())
                .destinationCity(checkIn.getDestination().getCity())
                .isVerified(checkIn.getIsVerified())
                .isActive(checkIn.getIsActive())
                .checkedInAt(checkIn.getCheckedInAt())
                .checkedOutAt(checkIn.getCheckedOutAt())
                .build();
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
