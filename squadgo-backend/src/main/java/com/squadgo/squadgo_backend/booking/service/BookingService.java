package com.squadgo.squadgo_backend.booking.service;

import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.auth.repository.UserRepository;
import com.squadgo.squadgo_backend.booking.dto.BookingResponse;
import com.squadgo.squadgo_backend.booking.dto.CreateBookingRequest;
import com.squadgo.squadgo_backend.booking.dto.ParticipantInfo;
import com.squadgo.squadgo_backend.booking.entity.Booking;
import com.squadgo.squadgo_backend.booking.entity.BookingParticipant;
import com.squadgo.squadgo_backend.booking.entity.BookingStatus;
import com.squadgo.squadgo_backend.booking.repository.BookingParticipantRepository;
import com.squadgo.squadgo_backend.booking.repository.BookingRepository;
import com.squadgo.squadgo_backend.destination.entity.Destination;
import com.squadgo.squadgo_backend.destination.repository.DestinationRepository;
import com.squadgo.squadgo_backend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingParticipantRepository bookingParticipantRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    // Setter injection with @Lazy to avoid potential circular dependency at startup
    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(@Lazy PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Transactional
    public BookingResponse createBooking(String email, CreateBookingRequest request) {
        User primaryUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        boolean isSplit = request.getIsSplitBooking() != null && request.getIsSplitBooking();

        Booking booking = Booking.builder()
                .primaryUser(primaryUser)
                .destination(destination)
                .bookingType(request.getBookingType())
                .status(BookingStatus.CONFIRMED)
                .partnerName(request.getPartnerName())
                .partnerContactNumber(request.getPartnerContactNumber())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalAmount(request.getTotalAmount())
                .numberOfPeople(request.getNumberOfPeople())
                .isSplitBooking(isSplit)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingParticipant> participants = new ArrayList<>();

        if (isSplit && request.getParticipantUserIds() != null && !request.getParticipantUserIds().isEmpty()) {
            int totalParticipants = request.getParticipantUserIds().size() + 1; // +1 for primary user
            int shareAmount = request.getTotalAmount() / totalParticipants;

            // Primary user participant — hasPaid = true (they initiated)
            BookingParticipant primaryParticipant = BookingParticipant.builder()
                    .booking(savedBooking)
                    .user(primaryUser)
                    .shareAmount(shareAmount)
                    .hasPaid(true)
                    .build();
            participants.add(bookingParticipantRepository.save(primaryParticipant));

            for (UUID participantId : request.getParticipantUserIds()) {
                User participant = userRepository.findById(participantId)
                        .orElseThrow(() -> new RuntimeException("Participant user not found: " + participantId));

                BookingParticipant bp = BookingParticipant.builder()
                        .booking(savedBooking)
                        .user(participant)
                        .shareAmount(shareAmount)
                        .hasPaid(false)
                        .build();
                participants.add(bookingParticipantRepository.save(bp));
            }
        }

        // Award travel coins: 1 coin per 100 INR spent (booking reward)
        int coinsToAward = request.getTotalAmount() / 100;
        if (coinsToAward > 0) {
            paymentService.awardTravelCoins(primaryUser, coinsToAward,
                    "Booking reward for booking at " + destination.getName());
        }

        return toResponse(savedBooking, participants);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByPrimaryUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(booking -> {
                    List<BookingParticipant> participants =
                            bookingParticipantRepository.findByBookingId(booking.getId());
                    return toResponse(booking, participants);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(String email, UUID bookingId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        boolean isPrimaryUser = booking.getPrimaryUser().getId().equals(user.getId());
        boolean isParticipant = bookingParticipantRepository.existsByBookingIdAndUserId(bookingId, user.getId());

        if (!isPrimaryUser && !isParticipant) {
            throw new RuntimeException("Access denied: You are not associated with this booking");
        }

        List<BookingParticipant> participants = bookingParticipantRepository.findByBookingId(bookingId);
        return toResponse(booking, participants);
    }

    @Transactional
    public BookingResponse cancelBooking(String email, UUID bookingId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getPrimaryUser().getId().equals(user.getId())) {
            throw new RuntimeException("Only the primary user can cancel this booking");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Only CONFIRMED bookings can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        List<BookingParticipant> participants = bookingParticipantRepository.findByBookingId(bookingId);
        return toResponse(saved, participants);
    }

    @Transactional
    public BookingResponse markParticipantPaid(String email, UUID bookingId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure booking exists
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        BookingParticipant participant = bookingParticipantRepository
                .findByBookingIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new RuntimeException("You are not a participant of this booking"));

        participant.setHasPaid(true);
        bookingParticipantRepository.save(participant);

        List<BookingParticipant> participants = bookingParticipantRepository.findByBookingId(bookingId);
        return toResponse(booking, participants);
    }

    // --- Helpers ---

    private BookingResponse toResponse(Booking booking, List<BookingParticipant> participants) {
        List<ParticipantInfo> participantInfos = participants.stream()
                .map(p -> ParticipantInfo.builder()
                        .userId(p.getUser().getId())
                        .userName(p.getUser().getFullName())
                        .shareAmount(p.getShareAmount())
                        .hasPaid(p.getHasPaid())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .primaryUserId(booking.getPrimaryUser().getId())
                .primaryUserName(booking.getPrimaryUser().getFullName())
                .destinationId(booking.getDestination().getId())
                .destinationName(booking.getDestination().getName())
                .destinationCity(booking.getDestination().getCity())
                .bookingType(booking.getBookingType())
                .status(booking.getStatus())
                .partnerName(booking.getPartnerName())
                .partnerContactNumber(booking.getPartnerContactNumber())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalAmount(booking.getTotalAmount())
                .numberOfPeople(booking.getNumberOfPeople())
                .isSplitBooking(booking.getIsSplitBooking())
                .participants(participantInfos)
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
