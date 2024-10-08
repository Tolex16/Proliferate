package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Chat.ChatMessage;
import com.proliferate.Proliferate.Domain.DTO.Chat.MessageType;
import com.proliferate.Proliferate.Domain.DTO.Chat.UserJoinRequest;
import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.Entities.Assignment;
import com.proliferate.Proliferate.Domain.Entities.ChatThread;
import com.proliferate.Proliferate.Domain.Entities.Notifications;
import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.ChatSentException;
import com.proliferate.Proliferate.ExeceptionHandler.EntityNotFoundException;
import com.proliferate.Proliferate.Repository.ChatThreadRepository;
import com.proliferate.Proliferate.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.proliferate.Proliferate.Domain.Entities.Message;
import com.proliferate.Proliferate.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
	
    @Autowired
    private MessageService messageService;

    private final MessageRepository messageRepository;
    private final ChatThreadRepository chatThreadRepository;
    //@MessageMapping("/chat.sendMessage")
    //@SendTo("/topic/public")
	@PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessage chatMessage) {
        String messageThreadId = chatMessage.getThreadId();
        try {
            // Your existing logic to save the message
            Message message = messageService.saveMessage(chatMessage);

            return ResponseEntity.ok("Chat sent successfully. Thread ID: " + message.getThread().getThreadId());
        } catch (IllegalArgumentException e) {
            // Handle any specific exceptions you want to throw manually
            throw new ChatSentException("Chat sent successfully. Thread ID: " + messageThreadId + chatMessage);
        }
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
	
	@PostMapping("/add-users/{studentId}/{tutorId}")
    public ResponseEntity<?> addUser(@PathVariable Long studentId, @PathVariable Long tutorId) {
     return ResponseEntity.ok(messageService.addUser(studentId,tutorId));
    }

    @PostMapping("/leave-chat/{studentId}/{tutorId}")
    public ResponseEntity<?> leaveChat(@PathVariable Long studentId, @PathVariable Long tutorId) {
        return ResponseEntity.ok(messageService.leaveChat(studentId, tutorId));
    }

	@GetMapping("/messages/{threadId}")
    public List<Message> getMessages(@PathVariable String threadId) {
        return messageService.getMessagesByThreadId(threadId);
    }
	
	@DeleteMapping("/clear-messages/{threadId}")
    public ResponseEntity<String> clearMessagesByThreadId(@PathVariable String threadId) {
    messageService.deleteMessagesByThreadId(threadId);
    return ResponseEntity.ok("Chat history cleared");
    }

   @DeleteMapping("/clear-message/{threadId}/{id}")
   public ResponseEntity<String> deleteMessageById(@PathVariable String threadId, @PathVariable Long id) {
    boolean deleted = messageService.deleteMessageByIdAndThreadId(id, threadId);
      if (deleted) {
          return ResponseEntity.ok("Message deleted");
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found");
      }
    }
	
	@DeleteMapping("/delete-thread/{threadId}")
    public ResponseEntity<?> deleteThread(@PathVariable String threadId) {
        messageService.deleteThread(threadId);
        return ResponseEntity.ok("ChatThread with threadId: " + threadId + " has been deleted.");
    }


    @GetMapping("/student-threads")
    public List<ChatThread> getChatThreadsByStudent() {
    return messageService.getChatThreadsByStudent();
    }

    @GetMapping("/tutor-threads")
    public List<ChatThread> getChatThreadsByTutor() {
        return messageService.getChatThreadsByTutor();
    }
}