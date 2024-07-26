package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Chat.ChatMessage;
import com.proliferate.Proliferate.Domain.Entities.Message;

import java.util.List;

public interface MessageService {
	
	List<Message> getMessagesBetweenUsers(Long senderId, Long receiverId);
	
	void sendMessage(ChatMessage chatMessage);
}
