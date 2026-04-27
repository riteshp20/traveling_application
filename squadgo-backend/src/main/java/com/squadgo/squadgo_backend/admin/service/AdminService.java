package com.squadgo.squadgo_backend.admin.service;

import com.squadgo.squadgo_backend.admin.dto.AdminStatsResponse;
import com.squadgo.squadgo_backend.admin.dto.AdminUserResponse;
import com.squadgo.squadgo_backend.admin.dto.UpdateUserRoleRequest;
import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.auth.repository.UserRepository;
import com.squadgo.squadgo_backend.destination.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    // -------------------------------------------------------------------------
    // User management
    // -------------------------------------------------------------------------

    public Page<AdminUserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable).map(this::toAdminUserResponse);
    }

    public AdminUserResponse getUserById(UUID userId) {
        User user = resolveUser(userId);
        return toAdminUserResponse(user);
    }

    public AdminUserResponse updateUserRole(UpdateUserRoleRequest request) {
        User user = resolveUser(request.getUserId());
        user.setRole(request.getNewRole());
        return toAdminUserResponse(userRepository.save(user));
    }

    public AdminUserResponse grantVerifiedBadge(UUID userId) {
        User user = resolveUser(userId);
        user.setIsVerified(true);
        return toAdminUserResponse(userRepository.save(user));
    }

    public AdminUserResponse revokeVerifiedBadge(UUID userId) {
        User user = resolveUser(userId);
        user.setIsVerified(false);
        return toAdminUserResponse(userRepository.save(user));
    }

    /**
     * Ban is not fully supported yet: the User entity does not have an
     * {@code isBanned} field. Add {@code isBanned} to User and a corresponding
     * column migration before implementing this method properly.
     */
    public void banUser(UUID userId) {
        throw new RuntimeException(
                "Ban feature requires an 'isBanned' field on the User entity. " +
                "Please add the field and a DB migration, then re-implement this method.");
    }

    public Page<AdminUserResponse> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository
                .findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(query, query, pageable)
                .map(this::toAdminUserResponse);
    }

    // -------------------------------------------------------------------------
    // Stats
    // -------------------------------------------------------------------------

    public AdminStatsResponse getSystemStats() {
        long totalUsers         = userRepository.count();
        long verifiedUsers      = userRepository.countByIsVerifiedTrue();
        long totalDestinations  = destinationRepository.count();
        long activeDestinations = destinationRepository.findByIsActiveTrue().size();

        // Booking and Review counts are placeholders until those repositories exist.
        long totalBookings = 0L;
        long totalReviews  = 0L;

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .verifiedUsers(verifiedUsers)
                .totalDestinations(totalDestinations)
                .activeDestinations(activeDestinations)
                .totalBookings(totalBookings)
                .totalReviews(totalReviews)
                .build();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private User resolveUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        return AdminUserResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .travelCoins(user.getTravelCoins())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
