package com.proliferate.Proliferate.Domain.DTO;

import com.proliferate.Proliferate.config.StrongPassword;
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
    @StrongPassword
    private String newPassword;

}
