package com.proliferate.Proliferate.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SessionResponse {

    private final Long sessionId;
    private final double amount;
}