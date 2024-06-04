package com.proliferate.Proliferate.Domain.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ChatMessage {
	
    private Long senderId;
	
    private Long receiverId;
	
    private String sender;
	
    private String receiver;
	
    private String senderType; // Either "Student" or "Tutor"
	
    private String receiverType; // Either "Student" or "Tutor"
	
    private String content;
	
    private String timestamp;

    private MessageType type;
}

