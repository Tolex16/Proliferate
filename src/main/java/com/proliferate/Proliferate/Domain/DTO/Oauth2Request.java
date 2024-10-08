package com.proliferate.Proliferate.Domain.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Oauth2Request {
    @NotBlank(message = "Id token is required")
    private String idToken;
}
