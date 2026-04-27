package com.squadgo.squadgo_backend.booking.entity;

import com.squadgo.squadgo_backend.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking_participants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer shareAmount;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hasPaid = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime joinedAt;
}
