package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Payment.PaymentRequest;
import com.proliferate.Proliferate.Response.EarningsHistoryResponse;
import com.proliferate.Proliferate.Response.PaymentHistoryResponse;
import com.proliferate.Proliferate.Response.StripeResponse;
import com.stripe.exception.StripeException;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    StripeResponse createPaymentIntent(PaymentRequest paymentRequest) throws StripeException;

	void handleFailedPayment(String paymentIntentId);
    List<PaymentHistoryResponse> getPaymentsByStudentId();
    List<PaymentHistoryResponse> getPaymentsByStudentIdAndDateRange(LocalDate startDate, LocalDate endDate);
    List<EarningsHistoryResponse> getPaymentsByTutorId();
    List<EarningsHistoryResponse> getPaymentsByTutorIdAndDateRange(LocalDate startDate, LocalDate endDate);
}
