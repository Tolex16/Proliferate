package com.proliferate.Proliferate.Domain.DTO.Chat;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatThreadDTO {
	
    private String threadId;
    
	private List<ChatMessage> messages;
}
