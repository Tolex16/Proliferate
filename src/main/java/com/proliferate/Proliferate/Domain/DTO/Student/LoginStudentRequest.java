package com.proliferate.Proliferate.Domain.DTO.Student;

import com.proliferate.Proliferate.config.StrongPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class LoginStudentRequest {
    private final String userName;

    @StrongPassword
    private final String password;
}
