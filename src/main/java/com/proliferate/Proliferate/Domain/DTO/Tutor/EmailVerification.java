package com.proliferate.Proliferate.Domain.DTO.Tutor;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerification {
	
	@Email(message = "Input a real email address")
    @JsonProperty("email")
    private String email;

}
