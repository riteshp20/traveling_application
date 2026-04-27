package com.squadgo.squadgo_backend.booking.controller;

import com.squadgo.squadgo_backend.booking.dto.BookingResponse;
import com.squadgo.squadgo_backend.booking.dto.CreateBookingRequest;
import com.squadgo.squadgo_backend.booking.service.BookingService;
import com.squadgo.squadgo_backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        BookingResponse response = bookingService.createBooking(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<BookingResponse> bookings = bookingService.getMyBookings(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID bookingId
    ) {
        BookingResponse response = bookingService.getBookingById(userDetails.getUsername(), bookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", response));
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID bookingId
    ) {
        BookingResponse response = bookingService.cancelBooking(userDetails.getUsername(), bookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", response));
    }

    @PutMapping("/{bookingId}/pay")
    public ResponseEntity<ApiResponse<BookingResponse>> markParticipantPaid(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID bookingId
    ) {
        BookingResponse response = bookingService.markParticipantPaid(userDetails.getUsername(), bookingId);
        return ResponseEntity.ok(ApiResponse.success("Payment marked successfully", response));
    }
}
