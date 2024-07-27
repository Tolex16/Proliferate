package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Chat.ChatMessage;
import com.proliferate.Proliferate.Domain.Entities.ChatThread;
import com.proliferate.Proliferate.Domain.Entities.Message;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.ChatThreadRepository;
import com.proliferate.Proliferate.Repository.MessageRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final MessageRepository messageRepository;
    private final ChatThreadRepository chatThreadRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Save a chat message to the database.
     */
	 
    public Message saveMessage(ChatMessage chatMessage) {
	ChatThread thread = chatThreadRepository.findByThreadId(chatMessage.getThreadId()).orElseGet(() -> {
        ChatThread newThread = new ChatThread();
        newThread.setThreadId(UUID.randomUUID().toString().substring(0, 6));
        return chatThreadRepository.save(newThread);
    });

        TutorEntity tutor = tutorRepository.findById(chatMessage.getReceiverId()).orElseThrow(() -> new UserNotFoundException("Tutor not present"));
        StudentEntity student = studentRepository.findById(chatMessage.getSenderId()).orElseThrow(() -> new UserNotFoundException("Student not present"));
        Message message = new Message();
        message.setStudent(student);
        message.setTutor(tutor);
        //if(studentRepository.existsByUserName(chatMessage.getSenderId()) || tutorRepository.exis(chatMessage.getReceiverId())){
          //  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is no account with this username.");
        //}
        message.setSenderFullName(student.getFirstName() + " " + student.getLastName());
        message.setReceiverFullName(tutor.getFirstName() + " " + tutor.getLastName());
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());
		message.setThread(thread);
        return messageRepository.save(message);
    }

    /**
     * Retrieve messages between two users.
     */
	 
//    public List<Message> getMessagesBetweenUsers(Long senderId, Long receiverId) {
//        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
//    }
	
	
	public List<Message> getMessagesByThreadId(String threadId) {
        return messageRepository.findByThread_ThreadId(threadId);
    }


    /**
     * Send a chat message using WebSocket and save it.
     */
    public void sendMessage(ChatMessage chatMessage) {
        Message message = saveMessage(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverFullName(),
                "/topic/messages",
                chatMessage
        );
    }

}
