package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Payment.PaymentRequest;
import com.proliferate.Proliferate.Response.StripeResponse;
import com.proliferate.Proliferate.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<StripeResponse> processPayment(@Validated @RequestBody PaymentRequest paymentRequest) {
        try {
            StripeResponse paymentIntent = paymentService.createPaymentIntent(paymentRequest);
            return ResponseEntity.ok(paymentIntent);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

@PostMapping("/fulfill/{paymentIntentId}")
    public ResponseEntity<Void> fulfillOrder(@PathVariable String paymentIntentId) {
        try {
            paymentService.fulfillOrder(paymentIntentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }
	
	    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PaymentRequest>> getPaymentsByStudentId(@PathVariable Long studentId) {
        List<PaymentRequest> payments = paymentService.getPaymentsByStudentId(studentId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/student/{studentId}/date-range")
    public ResponseEntity<List<PaymentRequest>> getPaymentsByStudentIdAndDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PaymentRequest> payments = paymentService.getPaymentsByStudentIdAndDateRange(studentId, startDate, endDate);
        return ResponseEntity.ok(payments);
    }
}

