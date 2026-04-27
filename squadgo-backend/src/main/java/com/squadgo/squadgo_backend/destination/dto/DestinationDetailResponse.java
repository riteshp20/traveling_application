package com.squadgo.squadgo_backend.destination.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DestinationDetailResponse {

    // --- Fields from DestinationSummaryResponse ---
    private UUID id;
    private String name;
    private String slug;
    private String city;
    private String state;
    private String category;
    private String imageUrl;
    private Integer minBudgetPerDay;
    private Integer maxBudgetPerDay;
    private Boolean isPremium;
    private String bestTimeToVisit;

    // --- Additional detail fields ---
    private String description;
    private Double latitude;
    private Double longitude;
    private List<String> tags;
    private String nearestStation;
    private List<String> stayOptions;
    private LocalDateTime createdAt;
}
