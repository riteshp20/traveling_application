package com.squadgo.squadgo_backend.destination.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationSummaryResponse {

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
}
