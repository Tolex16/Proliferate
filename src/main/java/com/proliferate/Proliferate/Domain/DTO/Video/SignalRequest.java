package com.proliferate.Proliferate.Domain.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SignalRequest {
    private String from;
    private String to;
    private String type; // offer, answer, candidate
    private String sdp;
    private String candidate;
    private String recipientId; // ID of the recipient user
}

