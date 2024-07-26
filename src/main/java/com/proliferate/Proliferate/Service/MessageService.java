package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Chat.ChatMessage;
import com.proliferate.Proliferate.Domain.Entities.Message;

import java.util.List;

public interface MessageService {
    Message saveMessage(ChatMessage chatMessage);
    List<Message> getMessagesBetweenUsers(Long senderId, Long receiverId);
    void sendMessage(ChatMessage chatMessage);
   // List<ChatThread> getAllThreads();
    List<Message> getMessagesByThreadId(String threadId);
}
