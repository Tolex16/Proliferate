package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Chat.ChatMessage;
import com.proliferate.Proliferate.Domain.DTO.Chat.MessageType;
import com.proliferate.Proliferate.Domain.Entities.ChatThread;
import com.proliferate.Proliferate.Repository.ChatThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.proliferate.Proliferate.Domain.Entities.Message;
import com.proliferate.Proliferate.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
	
   @Autowired
    private MessageService messageService;
    private final ChatThreadRepository chatThreadRepository;
    //@MessageMapping("/chat.sendMessage")
    //@SendTo("/topic/public")
	@PostMapping("/sendMessage")
    public ChatMessage sendMessage(@RequestBody ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now().toString());
		chatMessage.setType(MessageType.CHAT);
        messageService.sendMessage(chatMessage);
        return chatMessage;
    }
	

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderFullName());
        chatMessage.setContent("User joined");
        chatMessage.setTimestamp(LocalDateTime.now().toString());
		chatMessage.setType(MessageType.JOIN);
        return chatMessage;
    }


    // New endpoint to get messages between users
    //@GetMapping("/messages")
    //public List<Message> getMessages(@RequestParam Long senderId, @RequestParam Long receiverId) {
    //    return messageService.getMessagesBetweenUsers(senderId, receiverId);
   // }
	
	@GetMapping("/messages")
    public List<Message> getMessages(@RequestParam String threadId) {
        return messageService.getMessagesByThreadId(threadId);
    }

    @GetMapping("/threads")
    public List<ChatThread> getChatThreads() {
        return chatThreadRepository.findAll();
    }
	
}