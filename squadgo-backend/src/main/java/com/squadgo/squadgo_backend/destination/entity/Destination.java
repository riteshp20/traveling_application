package com.squadgo.squadgo_backend.destination.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "destinations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Builder.Default
    @Column(nullable = false)
    private String country = "India";

    private Double latitude;

    private Double longitude;

    /**
     * One of: MOUNTAIN, BEACH, HERITAGE, WILDLIFE, PILGRIMAGE, CITY, ADVENTURE
     */
    @Column(nullable = false)
    private String category;

    private String imageUrl;

    /**
     * Comma-separated tags, e.g. "snow,trekking,camping"
     */
    @Column(columnDefinition = "TEXT")
    private String tags;

    private Integer minBudgetPerDay;

    private Integer maxBudgetPerDay;

    private String nearestStation;

    /**
     * Comma-separated stay options, e.g. "hostel,hotel,camping"
     */
    @Column(columnDefinition = "TEXT")
    private String stayOptions;

    private String bestTimeToVisit;

    /**
     * Premium destinations require extra TravelCoins to view full details.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isPremium = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
