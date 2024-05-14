package com.proliferate.Proliferate.Domain.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Password required")
    private String currentPassword;

    @NotBlank(message = "Password required")
    private String newPassword;

    @NotBlank(message = "Password required")
    private String confirmNewPassword;
}
