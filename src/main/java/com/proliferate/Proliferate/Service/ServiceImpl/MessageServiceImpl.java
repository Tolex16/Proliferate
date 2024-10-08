package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Chat.ChatMessage;
import com.proliferate.Proliferate.Domain.DTO.Chat.MessageType;
import com.proliferate.Proliferate.Domain.DTO.Chat.UserJoinRequest;
import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.ExeceptionHandler.EntityNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final MessageRepository messageRepository;
    private final ChatThreadRepository chatThreadRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private final JwtService jwtService;

    /**
     * Save a chat message to the database.
     */
	 @Transactional
    public Message saveMessage(ChatMessage chatMessage) {
    // Retrieve or Create Chat Thread
    ChatThread thread = chatThreadRepository.findByThreadId(chatMessage.getThreadId())
        .orElseGet(() -> {
            ChatThread newThread = new ChatThread();
            newThread.setThreadId(UUID.randomUUID().toString().substring(0, 6));

            StudentEntity student = studentRepository.findById(chatMessage.getSenderId()).orElse(null);
            TutorEntity tutor = tutorRepository.findById(chatMessage.getReceiverId()).orElse(null);

            if (student == null && tutor == null) {
                student = studentRepository.findById(chatMessage.getReceiverId()).orElseThrow(() -> new UserNotFoundException("Student not found"));
                tutor = tutorRepository.findById(chatMessage.getSenderId()).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
            } else if (student == null) {
                student = studentRepository.findById(chatMessage.getReceiverId()).orElseThrow(() -> new UserNotFoundException("Student not found"));
            } else if (tutor == null) {
                tutor = tutorRepository.findById(chatMessage.getSenderId()).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
            }

            newThread.setStudent(student);
            newThread.setTutor(tutor);

            return chatThreadRepository.save(newThread);
        });

    // Extract the student and tutor involved in this thread
    StudentEntity student = thread.getStudent();
    TutorEntity tutor = thread.getTutor();

    if (!threadHasUser(thread, student.getStudentId(), tutor.getTutorId())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid chat thread for the provided user IDs.");
    }

    // Validate that the sender and receiver IDs match the student and tutor in this thread
    Long senderId = chatMessage.getSenderId();
    Long receiverId = chatMessage.getReceiverId();

    boolean isValidSender = senderId.equals(student.getStudentId()) || senderId.equals(tutor.getTutorId());
    boolean isValidReceiver = receiverId.equals(student.getStudentId()) || receiverId.equals(tutor.getTutorId());

    if (!isValidSender || !isValidReceiver) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sender or receiver is not authorized in this chat thread.");
    }

    Message message = new Message();
    message.setStudent(student);
    message.setTutor(tutor);

    // Set the correct full names based on actual sender and receiver roles
    if (senderId.equals(student.getStudentId())) {
        message.setSenderFullName(student.getFirstName() + " " + student.getLastName());
        message.setReceiverFullName(tutor.getFirstName() + " " + tutor.getLastName());
    } else {
        message.setSenderFullName(tutor.getFirstName() + " " + tutor.getLastName());
        message.setReceiverFullName(student.getFirstName() + " " + student.getLastName());
    }

    message.setSenderId(chatMessage.getSenderId());
    message.setContent(chatMessage.getContent());
    message.setTimestamp(LocalDateTime.now());
    message.setThread(thread);
    message.setType(MessageType.CHAT);

    return messageRepository.save(message);
}


   private boolean threadHasUser(ChatThread thread, Long studentId, Long tutorId) {
    StudentEntity threadStudent = thread.getStudent();
    TutorEntity threadTutor = thread.getTutor();

    if (Objects.equals(threadStudent.getStudentId(), studentId) && Objects.equals(threadTutor.getTutorId(), tutorId)) {
        return true;
    }

    for (Message message : thread.getMessages()) {
        if (Objects.equals(message.getStudent().getStudentId(), studentId)
            && Objects.equals(message.getTutor().getTutorId(), tutorId)) {
            return true;
        }
    }

    return false;
    }
	public List<Message> getMessagesByThreadId(String threadId) {
        return messageRepository.findByThread_ThreadId(threadId);
    }

public ResponseEntity<ChatMessage> addUser(@PathVariable Long studentId, @PathVariable Long tutorId) {
    // Fetch the student and tutor entities based on the provided IDs
    StudentEntity student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

    TutorEntity tutor = tutorRepository.findById(tutorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found"));

    // Create a ChatMessage for the response
    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setContent(student.getFirstName() + " " + student.getLastName() + " joined the chat.<br>" +
                   tutor.getFirstName() + " " + tutor.getLastName() + " joined the chat.");

    chatMessage.setTimestamp(String.valueOf(LocalDateTime.now()));
    chatMessage.setType(MessageType.JOIN);

    return ResponseEntity.ok(chatMessage);
}

   public ResponseEntity<ChatMessage> leaveChat(Long studentId, Long tutorId) {
    // Fetch the student and tutor entities based on the provided IDs
    StudentEntity student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

    TutorEntity tutor = tutorRepository.findById(tutorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found"));

    // Create a ChatMessage for the response
    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setContent(student.getFirstName() + " " + student.getLastName() + " left the chat.<br>" +
                   tutor.getFirstName() + " " + tutor.getLastName() + " left the chat.");

    chatMessage.setTimestamp(String.valueOf(LocalDateTime.now()));
    chatMessage.setType(MessageType.LEAVE);

    return ResponseEntity.ok(chatMessage);
   }

   public void deleteMessagesByThreadId(String threadId) {
    List<Message> messages = messageRepository.findByThread_ThreadId(threadId);
    if (!messages.isEmpty()) {
        messageRepository.deleteAll(messages);
    }
   }

   public boolean deleteMessageByIdAndThreadId(Long id, String threadId) {
    Optional<Message> messageOpt = messageRepository.findByIdAndThread_ThreadId(id, threadId);
    if (messageOpt.isPresent()) {
        messageRepository.delete(messageOpt.get());
        return true;
    }
    return false;
   }
   
    @Transactional
    public void deleteThread(String threadId) {
        List<Message> remainingMessages = messageRepository.findByThread_ThreadId(threadId);

        if (!remainingMessages.isEmpty()) {
            throw new IllegalStateException("Messages still exist for this thread. Clear messages first.");
        }

        Optional<ChatThread> chatThreadOpt = chatThreadRepository.findByThreadId(threadId);
        if (chatThreadOpt.isPresent()) {
            chatThreadRepository.deleteByThreadId(threadId);
        } else {
            throw new EntityNotFoundException("Chat not found with threadId: " + threadId);
        }
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

    public List<ChatThread> getChatThreadsByStudent() {
	Long studentId = jwtService.getUserId();
    return chatThreadRepository.findAllByStudent_StudentId(studentId);
    }

    public List<ChatThread> getChatThreadsByTutor() {
        Long tutorId = jwtService.geTutorId();
        return chatThreadRepository.findAllByTutor_TutorId(tutorId);
    }
}
