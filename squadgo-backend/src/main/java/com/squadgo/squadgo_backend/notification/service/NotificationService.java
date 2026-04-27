package com.squadgo.squadgo_backend.notification.service;

import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.auth.repository.UserRepository;
import com.squadgo.squadgo_backend.notification.dto.NotificationResponse;
import com.squadgo.squadgo_backend.notification.entity.Notification;
import com.squadgo.squadgo_backend.notification.entity.NotificationType;
import com.squadgo.squadgo_backend.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // -------------------------------------------------------------------------
    // Internal helper — called by other services (MatchService, BookingService…)
    // -------------------------------------------------------------------------

    /**
     * Creates and persists a Notification for the given recipient.
     * In production this would also trigger an FCM push; for now it only
     * saves to the database.
     */
    public Notification sendNotification(User recipient,
                                         NotificationType type,
                                         String title,
                                         String body,
                                         String referenceId) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .body(body)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        // TODO (production): fire FCM push notification here before saving.
        return notificationRepository.save(notification);
    }

    // -------------------------------------------------------------------------
    // User-facing operations
    // -------------------------------------------------------------------------

    public List<NotificationResponse> getMyNotifications(String email) {
        User user = resolveUser(email);
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getUnreadNotifications(String email) {
        User user = resolveUser(email);
        return notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount(String email) {
        User user = resolveUser(email);
        return notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public void markAllAsRead(String email) {
        User user = resolveUser(email);
        notificationRepository.markAllAsRead(user.getId());
    }

    @Transactional
    public void markOneAsRead(String email, UUID notificationId) {
        User user = resolveUser(email);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: notification does not belong to the current user.");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .referenceId(n.getReferenceId())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
