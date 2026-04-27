package com.squadgo.squadgo_backend.notification.dto;

import com.squadgo.squadgo_backend.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private UUID notificationId;
    private NotificationType type;
    private String title;
    private String body;
    private String referenceId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
