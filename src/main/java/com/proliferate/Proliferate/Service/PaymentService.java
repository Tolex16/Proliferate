package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Payment.PaymentRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {
    PaymentIntent createPaymentIntent(PaymentRequest paymentRequest) throws StripeException;
    void fulfillOrder(String paymentIntentId);
	void handleFailedPayment(String paymentIntentId);
    List<PaymentRequest> getPaymentsByStudentId(Long studentId);
    List<PaymentRequest> getPaymentsByStudentIdAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate);
}
