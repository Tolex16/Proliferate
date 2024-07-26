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
	
	private String senderFullName;
    
	private String receiverFullName;
	
    private String content;
	
    private String timestamp;

    private MessageType type;
	
	private String threadId;
}

