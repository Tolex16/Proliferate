package com.proliferate.Proliferate.Domain.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Verify2FARequest {

    @NotBlank(message = "Email required")
    @Email
	private String email;

    @NotBlank(message = "Code required")
    private String code;

}
