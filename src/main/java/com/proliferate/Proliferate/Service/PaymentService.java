package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Payment.PaymentRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface PaymentService {
    PaymentIntent createPaymentIntent(PaymentRequest paymentRequest) throws StripeException;
    void fulfillOrder(String paymentIntentId);
	void handleFailedPayment(String paymentIntentId);
}
