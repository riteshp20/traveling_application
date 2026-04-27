package com.squadgo.squadgo_backend.booking.repository;

import com.squadgo.squadgo_backend.booking.entity.BookingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingParticipantRepository extends JpaRepository<BookingParticipant, UUID> {

    List<BookingParticipant> findByBookingId(UUID bookingId);

    List<BookingParticipant> findByUserId(UUID userId);

    Optional<BookingParticipant> findByBookingIdAndUserId(UUID bookingId, UUID userId);

    boolean existsByBookingIdAndUserId(UUID bookingId, UUID userId);
}
