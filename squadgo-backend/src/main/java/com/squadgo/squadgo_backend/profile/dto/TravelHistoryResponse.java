package com.squadgo.squadgo_backend.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelHistoryResponse {

    private UUID id;
    private String destination;
    private String city;
    private String state;
    private String country;
    private LocalDate visitedOn;
    private String notes;
}
