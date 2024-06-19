package com.proliferate.Proliferate.Domain.DTO.Tutor;

import com.proliferate.Proliferate.config.StrongPassword;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class LoginTutorRequest {

    @Email(message = "Email should be valid")
    private final String email;

    @StrongPassword
    private final String password;
}
