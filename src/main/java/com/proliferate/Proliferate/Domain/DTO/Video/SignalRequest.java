package com.proliferate.Proliferate.Domain.DTO.Video;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalRequest {
    private String from;
    private String to;
    private String type; // offer, answer, candidate
    private String sdp;
    private String candidate;
    private String recipientId; // ID of the recipient user
}