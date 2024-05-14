package com.proliferate.Proliferate.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class LoginRequest {
    private final String userName;

    private final String password;
}
