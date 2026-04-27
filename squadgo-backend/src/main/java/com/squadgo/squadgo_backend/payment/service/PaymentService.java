package com.squadgo.squadgo_backend.payment.service;

import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.auth.repository.UserRepository;
import com.squadgo.squadgo_backend.booking.entity.Booking;
import com.squadgo.squadgo_backend.booking.entity.BookingStatus;
import com.squadgo.squadgo_backend.booking.repository.BookingRepository;
import com.squadgo.squadgo_backend.payment.dto.InitiatePaymentRequest;
import com.squadgo.squadgo_backend.payment.dto.PaymentResponse;
import com.squadgo.squadgo_backend.payment.dto.TravelCoinTransactionResponse;
import com.squadgo.squadgo_backend.payment.dto.VerifyPaymentRequest;
import com.squadgo.squadgo_backend.payment.entity.PaymentPurpose;
import com.squadgo.squadgo_backend.payment.entity.PaymentStatus;
import com.squadgo.squadgo_backend.payment.entity.PaymentTransaction;
import com.squadgo.squadgo_backend.payment.entity.TravelCoinTransaction;
import com.squadgo.squadgo_backend.payment.repository.PaymentTransactionRepository;
import com.squadgo.squadgo_backend.payment.repository.TravelCoinTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final TravelCoinTransactionRepository travelCoinTransactionRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public PaymentResponse initiatePayment(String email, InitiatePaymentRequest request) {
        User payer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = null;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
        }

        // Generate mock Razorpay order ID
        String razorpayOrderId = "order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        PaymentTransaction transaction = PaymentTransaction.builder()
                .payer(payer)
                .booking(booking)
                .razorpayOrderId(razorpayOrderId)
                .amount(request.getAmount())
                .status(PaymentStatus.INITIATED)
                .description(request.getDescription())
                .currency("INR")
                .purpose(request.getPurpose())
                .build();

        PaymentTransaction saved = paymentTransactionRepository.save(transaction);
        return toPaymentResponse(saved);
    }

    @Transactional
    public PaymentResponse verifyAndCompletePayment(String email, VerifyPaymentRequest request) {
        User payer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentTransaction transaction = paymentTransactionRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Transaction not found for order: " + request.getRazorpayOrderId()));

        if (!transaction.getPayer().getId().equals(payer.getId())) {
            throw new RuntimeException("Access denied: This transaction does not belong to you");
        }

        // Mock signature verification — in production, use HMAC-SHA256 with Razorpay secret key
        // For now, we trust the provided paymentId
        transaction.setRazorpayPaymentId(request.getRazorpayPaymentId());
        transaction.setStatus(PaymentStatus.SUCCESS);

        // Post-payment side effects
        if (transaction.getPurpose() == PaymentPurpose.BOOKING && transaction.getBooking() != null) {
            Booking booking = transaction.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        }

        if (transaction.getPurpose() == PaymentPurpose.TRAVEL_COINS) {
            // 1 coin per INR 1 spent on travel coins purchase
            int coins = transaction.getAmount();
            awardTravelCoins(payer, coins, "Purchased " + coins + " Travel Coins");
        }

        PaymentTransaction saved = paymentTransactionRepository.save(transaction);

        // Refresh payer from DB to get updated coin balance
        userRepository.save(payer);

        return toPaymentResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyTransactions(String email) {
        User payer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return paymentTransactionRepository.findByPayerIdOrderByCreatedAtDesc(payer.getId())
                .stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer getTravelCoinBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getTravelCoins() != null ? user.getTravelCoins() : 0;
    }

    @Transactional(readOnly = true)
    public List<TravelCoinTransactionResponse> getCoinHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return travelCoinTransactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toCoinResponse)
                .collect(Collectors.toList());
    }

    /**
     * Awards travel coins to a user and records the transaction.
     * Used internally by BookingService and verify payment flow.
     */
    @Transactional
    public void awardTravelCoins(User user, Integer coins, String reason) {
        if (coins == null || coins <= 0) {
            return;
        }

        // Refresh user from DB to get latest state
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        int currentCoins = managedUser.getTravelCoins() != null ? managedUser.getTravelCoins() : 0;
        managedUser.setTravelCoins(currentCoins + coins);
        userRepository.save(managedUser);

        TravelCoinTransaction coinTransaction = TravelCoinTransaction.builder()
                .user(managedUser)
                .coins(coins)
                .reason(reason)
                .build();
        travelCoinTransactionRepository.save(coinTransaction);
    }

    // --- Helpers ---

    private PaymentResponse toPaymentResponse(PaymentTransaction transaction) {
        return PaymentResponse.builder()
                .transactionId(transaction.getId())
                .razorpayOrderId(transaction.getRazorpayOrderId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus())
                .purpose(transaction.getPurpose())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private TravelCoinTransactionResponse toCoinResponse(TravelCoinTransaction tx) {
        return TravelCoinTransactionResponse.builder()
                .id(tx.getId())
                .coins(tx.getCoins())
                .reason(tx.getReason())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
