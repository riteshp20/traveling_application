package com.squadgo.squadgo_backend.payment.controller;

import com.squadgo.squadgo_backend.common.dto.ApiResponse;
import com.squadgo.squadgo_backend.payment.dto.InitiatePaymentRequest;
import com.squadgo.squadgo_backend.payment.dto.PaymentResponse;
import com.squadgo.squadgo_backend.payment.dto.TravelCoinTransactionResponse;
import com.squadgo.squadgo_backend.payment.dto.VerifyPaymentRequest;
import com.squadgo.squadgo_backend.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody InitiatePaymentRequest request
    ) {
        PaymentResponse response = paymentService.initiatePayment(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Payment initiated successfully", response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyAndCompletePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody VerifyPaymentRequest request
    ) {
        PaymentResponse response = paymentService.verifyAndCompletePayment(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Payment verified and completed successfully", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<PaymentResponse> transactions = paymentService.getMyTransactions(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Payment history retrieved successfully", transactions));
    }

    @GetMapping("/coins/balance")
    public ResponseEntity<ApiResponse<Integer>> getTravelCoinBalance(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer balance = paymentService.getTravelCoinBalance(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Travel coin balance retrieved successfully", balance));
    }

    @GetMapping("/coins/history")
    public ResponseEntity<ApiResponse<List<TravelCoinTransactionResponse>>> getCoinHistory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<TravelCoinTransactionResponse> history = paymentService.getCoinHistory(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Coin history retrieved successfully", history));
    }
}
