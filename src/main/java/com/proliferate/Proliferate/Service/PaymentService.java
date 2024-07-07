package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Payment.PaymentRequest;
import com.proliferate.Proliferate.Response.PaymentHistoryResponse;
import com.proliferate.Proliferate.Response.StripeResponse;
import com.stripe.exception.StripeException;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    StripeResponse createPaymentIntent(PaymentRequest paymentRequest) throws StripeException;
    void fulfillOrder(String paymentIntentId);
	void handleFailedPayment(String paymentIntentId);
    List<PaymentHistoryResponse> getPaymentsByStudentId(Long studentId);
    List<PaymentHistoryResponse> getPaymentsByStudentIdAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate);
}
