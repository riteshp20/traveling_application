package com.squadgo.squadgo_backend.destination.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDestinationRequest {

    @NotBlank(message = "Destination name is required")
    private String name;

    private String description;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @Builder.Default
    private String country = "India";

    private Double latitude;

    private Double longitude;

    @NotBlank(message = "Category is required")
    private String category;

    private String imageUrl;

    /**
     * Tags as a list; will be stored as comma-separated string.
     * Example: ["snow", "trekking", "camping"]
     */
    private List<String> tags;

    private Integer minBudgetPerDay;

    private Integer maxBudgetPerDay;

    private String nearestStation;

    /**
     * Stay options as a list; will be stored as comma-separated string.
     * Example: ["hostel", "hotel", "camping"]
     */
    private List<String> stayOptions;

    private String bestTimeToVisit;

    @Builder.Default
    private Boolean isPremium = false;
}
