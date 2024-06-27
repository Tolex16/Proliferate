package com.proliferate.Proliferate.Controller;


import com.proliferate.Proliferate.Service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentMethod;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final PaymentService paymentService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(event);
                break;
				case "payment_method.attached":
                handlePaymentMethodAttached(event);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unhandled event type" + event.getType());
        }

        return ResponseEntity.ok("Event processed");
    }

    private void handlePaymentIntentSucceeded(Event event) {
        // Extract the payment intent ID and fulfill the order
        String paymentIntentId = event.getDataObjectDeserializer().getObject().get().toJson();
        paymentService.fulfillOrder(paymentIntentId);
    }

    private void handlePaymentIntentFailed(Event event) {
        // Handle payment failure
        String paymentIntentId = event.getDataObjectDeserializer().getObject().get().toJson();
        paymentService.handleFailedPayment(paymentIntentId);
    }
	
	private void handlePaymentMethodAttached(Event event) {
        PaymentMethod paymentMethod = (PaymentMethod) event.getData().getPreviousAttributes();
        // Handle the event
		String paymentIntentId = paymentMethod.getId();
        paymentService.fulfillOrder(paymentIntentId);
    }
}

