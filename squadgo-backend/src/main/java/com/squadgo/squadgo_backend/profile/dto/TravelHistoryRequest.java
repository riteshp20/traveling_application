package com.squadgo.squadgo_backend.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelHistoryRequest {

    @NotBlank(message = "Destination is required")
    private String destination;

    private String city;

    private String state;

    @NotBlank(message = "Country is required")
    @Builder.Default
    private String country = "India";

    @NotNull(message = "Visit date is required")
    private LocalDate visitedOn;

    private String notes;
}
