package com.example.read_book_online.controller;

import com.example.read_book_online.dto.request.MomoRequest;
import com.example.read_book_online.service.impl.MomoPaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/momo")
public class PaymentController {
    private final MomoPaymentServiceImpl momoService;

    public PaymentController(MomoPaymentServiceImpl momoService) {
        this.momoService = momoService;
    }

    @Operation(summary = "Create MoMo Payment",
            description = "Receives an amount and calls MoMo API to get payment QR link")
    @PostMapping
    public ResponseEntity<String> createPayment(@Valid @RequestBody MomoRequest paymentRequest) {
        String response = momoService.createPaymentRequest(paymentRequest.getAmount());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check Payment Status",
            description = "Checks the status of a MoMo payment using orderId")
    @GetMapping("/order-status/{orderId}")
    public ResponseEntity<String> checkPaymentStatus(@PathVariable String orderId) {
        String response = momoService.checkPaymentStatus(orderId);
        return ResponseEntity.ok(response);
    }
}