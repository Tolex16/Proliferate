package com.proliferate.Proliferate.Domain.DTO.Payment;

import com.proliferate.Proliferate.Domain.Entities.Currency;
import com.proliferate.Proliferate.Domain.Entities.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentRequest {

    @NotNull
    private Long sessionId;

    @NotNull
    @Min(0)
    private double amount;
	
	@NotNull
    private String token; // The token received from Stripe.js
	
	@NotNull
	private Currency currency;
	
	@NotBlank
	private String paymentMethod;
	
    private String transactionId;

	private String description;
	
	private Status status;
	
	private LocalDate date;
	
	private String bankName;
	
    private String accountName;
    
	private String accountNumber;
    
	private String routingNumber;
}
