package com.squadgo.squadgo_backend.profile.entity;

import com.squadgo.squadgo_backend.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "travel_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String destination;

    private String city;

    private String state;

    @Column(nullable = false)
    @Builder.Default
    private String country = "India";

    @Column(nullable = false)
    private LocalDate visitedOn;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
