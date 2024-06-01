package com.proliferate.Proliferate.Domain.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ResetPassword {

    @NotBlank(message = "Token required")
    private String token;

    @NotBlank(message = "New password required")
    private String newPassword;

}
