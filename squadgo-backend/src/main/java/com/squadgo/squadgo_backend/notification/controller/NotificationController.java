package com.squadgo.squadgo_backend.notification.controller;

import com.squadgo.squadgo_backend.common.dto.ApiResponse;
import com.squadgo.squadgo_backend.notification.dto.NotificationResponse;
import com.squadgo.squadgo_backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /api/v1/notifications
     * Returns all notifications for the authenticated user, newest first.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<NotificationResponse> notifications =
                notificationService.getMyNotifications(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully.", notifications));
    }

    /**
     * GET /api/v1/notifications/unread
     * Returns only unread notifications, newest first.
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<NotificationResponse> notifications =
                notificationService.getUnreadNotifications(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Unread notifications fetched successfully.", notifications));
    }

    /**
     * GET /api/v1/notifications/unread/count
     * Returns the count of unread notifications.
     */
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long count = notificationService.getUnreadCount(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Unread count fetched successfully.", count));
    }

    /**
     * PUT /api/v1/notifications/read-all
     * Marks every notification for the authenticated user as read.
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {

        notificationService.markAllAsRead(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read.", null));
    }

    /**
     * PUT /api/v1/notifications/{notificationId}/read
     * Marks a single notification as read (only if it belongs to the current user).
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markOneAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID notificationId) {

        notificationService.markOneAsRead(userDetails.getUsername(), notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read.", null));
    }
}
