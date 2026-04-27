package com.squadgo.squadgo_backend.checkin.entity;

import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.destination.entity.Destination;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "check_ins")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @Column(nullable = false)
    private Double userLatitude;

    @Column(nullable = false)
    private Double userLongitude;

    /**
     * True if the user's GPS coordinates were within 5 km of the destination at check-in time.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isVerified = false;

    /**
     * True while the user is still present at the destination (not yet checked out).
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime checkedInAt;

    /**
     * Null means the user has not checked out yet.
     */
    private LocalDateTime checkedOutAt;
}
