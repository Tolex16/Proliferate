package com.proliferate.Proliferate.Domain.DTO;

import com.proliferate.Proliferate.config.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Current password required")
    private String currentPassword;

    @NotBlank(message = "New password required")
    @StrongPassword
    private String newPassword;

    @NotBlank(message = "Confirm new password required")
    @StrongPassword
    private String confirmNewPassword;
}
