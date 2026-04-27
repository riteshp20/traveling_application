package com.squadgo.squadgo_backend.booking.dto;

import com.squadgo.squadgo_backend.booking.entity.BookingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest {

    @NotNull
    private UUID destinationId;

    @NotNull
    private BookingType bookingType;

    @NotBlank
    private String partnerName;

    private String partnerContactNumber;

    @NotNull
    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    @NotNull
    @Min(1)
    private Integer totalAmount;

    @NotNull
    @Min(1)
    private Integer numberOfPeople;

    private Boolean isSplitBooking;

    private List<UUID> participantUserIds;
}
