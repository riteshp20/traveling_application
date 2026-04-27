package com.squadgo.squadgo_backend.booking.dto;

import com.squadgo.squadgo_backend.booking.entity.BookingStatus;
import com.squadgo.squadgo_backend.booking.entity.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private UUID bookingId;
    private UUID primaryUserId;
    private String primaryUserName;

    private UUID destinationId;
    private String destinationName;
    private String destinationCity;

    private BookingType bookingType;
    private BookingStatus status;

    private String partnerName;
    private String partnerContactNumber;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private Integer totalAmount;
    private Integer numberOfPeople;
    private Boolean isSplitBooking;

    private List<ParticipantInfo> participants;

    private LocalDateTime createdAt;
}
