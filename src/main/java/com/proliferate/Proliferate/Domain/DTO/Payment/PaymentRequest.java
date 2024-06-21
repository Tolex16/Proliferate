package com.proliferate.Proliferate.Domain.DTO.Payment;

import com.proliferate.Proliferate.Domain.Entities.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentRequest {
    @NotNull
    private Long studentId;
    
    @NotNull
    private Long subjectId;
    
    @NotNull
    @Min(0)
    private double amount;
    
	@NotBlank
	private Currency currency;
	    
    @NotBlank
    @Pattern(regexp = "\\d{16}")
    private String cardNumber;
    
    @NotBlank
    @Pattern(regexp = "(0[1-9]|1[0-2])/(\\d{4})")
    private String expiration;
    
    @NotBlank
    @Pattern(regexp = "\\d{3}")
    private String cvv;
}
