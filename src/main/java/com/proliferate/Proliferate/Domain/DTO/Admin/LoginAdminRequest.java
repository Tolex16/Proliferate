package com.proliferate.Proliferate.Domain.DTO.Admin;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginAdminRequest {
    @Email(message = "Email should be valid")
    private final String email;


    private final String password;
}
