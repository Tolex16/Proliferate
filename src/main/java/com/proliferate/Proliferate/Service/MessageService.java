package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Chat.ChatMessage;
import com.proliferate.Proliferate.Domain.DTO.Chat.UserJoinRequest;
import com.proliferate.Proliferate.Domain.Entities.ChatThread;
import com.proliferate.Proliferate.Domain.Entities.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MessageService {
    Message saveMessage(ChatMessage chatMessage);

    void sendMessage(ChatMessage chatMessage);

    List<ChatThread> getChatThreadsByTutor();
    List<ChatThread> getChatThreadsByStudent();
    ResponseEntity<ChatMessage> addUser(Long studentId, Long tutorId);
    ResponseEntity<ChatMessage> leaveChat(Long studentId, Long tutorId);
    List<Message> getMessagesByThreadId(String threadId);

    void deleteMessagesByThreadId(String threadId);
	
	void deleteThread(String threadId);

    boolean deleteMessageByIdAndThreadId(Long id, String threadId);
}
