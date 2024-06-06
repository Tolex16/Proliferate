package com.proliferate.Proliferate.Domain.DTO.Tutor;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EmailVerification {
	
	@Email(message = "Input a real email address")
    private String email;

}
