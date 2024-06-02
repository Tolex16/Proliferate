package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Chat.ChatMessage;
import com.proliferate.Proliferate.Domain.Entities.Message;
import com.proliferate.Proliferate.Repository.MessageRepository;
import com.proliferate.Proliferate.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Save a chat message to the database.
     */
	 
    public Message saveMessage(ChatMessage chatMessage) {
        Message message = new Message();
        message.setSenderId(Long.parseLong(chatMessage.getSender()));
        message.setReceiverId(Long.parseLong(chatMessage.getReceiver()));
        message.setSenderType(chatMessage.getSenderType());
        message.setReceiverType(chatMessage.getReceiverType());
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    /**
     * Retrieve messages between two users.
     */
	 
    public List<Message> getMessagesBetweenUsers(Long senderId, Long receiverId) {
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }


    /**
     * Send a chat message using WebSocket and save it.
     */
    public void sendMessage(ChatMessage chatMessage) {
        Message message = saveMessage(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(),
                "/topic/messages",
                chatMessage
        );
    }

}
