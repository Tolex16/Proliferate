package com.proliferate.Proliferate.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.proliferate.Proliferate.Domain.DTO.Video.CallRequest;
import com.proliferate.Proliferate.Domain.DTO.Video.SignalRequest;
import com.proliferate.Proliferate.Domain.Entities.Notifications;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.NotificationRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.config.VideoCallWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/call")
@RequiredArgsConstructor
public class VideoCallController {

    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final NotificationRepository notificationRepository;
    private final VideoCallWebSocketHandler videoCallWebSocketHandler;

    private Map<String, CallRequest> ongoingCalls = new HashMap<>();

    @PostMapping("/start")
    public ResponseEntity<String> startCall(@RequestBody CallRequest request) {
        // Validate the caller and callee
        if (request.getCaller() == null || request.getCallee() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Caller and Callee must be specified");
        }
        if (!studentRepository.existsByUserNameIgnoreCase(request.getCaller()) && !tutorRepository.existsByEmailIgnoreCase(request.getCaller())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Caller not found");
        }
        if (!studentRepository.existsByUserNameIgnoreCase(request.getCallee()) && !tutorRepository.existsByEmailIgnoreCase(request.getCallee())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Callee not found");
        }

        // Notify the callee of the incoming call
        Map<String, WebSocketSession> sessions = videoCallWebSocketHandler.getSessions();
        WebSocketSession calleeSession = sessions.get(request.getCallee());

        if (calleeSession != null && calleeSession.isOpen()) {
            CallRequest call = new CallRequest(request.getCaller(), request.getCallee());
            ongoingCalls.put(request.getCaller(), call);

            try {
                calleeSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(call)));
                return ResponseEntity.ok("Call started between " + request.getCaller() + " and " + request.getCallee());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to notify callee");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Callee not connected");
        }
    }

    @PostMapping("/end")
    public ResponseEntity<String> endCall(@RequestBody CallRequest request) {
        // Validate the caller and callee
        if (request.getCaller() == null || request.getCallee() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Caller and Callee must be specified");
        }
        if (!studentRepository.existsByUserNameIgnoreCase(request.getCaller()) && !tutorRepository.existsByEmailIgnoreCase(request.getCaller())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Caller not found");
        }
        if (!studentRepository.existsByUserNameIgnoreCase(request.getCallee()) && !tutorRepository.existsByEmailIgnoreCase(request.getCallee())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Callee not found");
        }
        
        Notifications notification = new Notifications();
        StudentEntity student = studentRepository.findByUserNameIgnoreCaseAndEmailVerifiedIsTrue(request.getCaller()).orElseThrow(() -> new UserNotFoundException("Student not present"));
        TutorEntity tutor = tutorRepository.findByEmailIgnoreCaseAndEmailVerifiedIsTrue(request.getCallee()).orElseThrow(() -> new UserNotFoundException("Tutor not present"));
        notification.setStudent(student);
        notification.setType("Review and Rating Request");
        notification.setMessage( "Feedback requested: Please provide a review and rating for your recent tutoring session with " + tutor.getFirstName() + " " + tutor.getLastName() + ". Your feedback is valuable.");
        notificationRepository.save(notification);

        // Notify both parties that the call has ended
        CallRequest call = ongoingCalls.remove(request.getCaller());
        if (call != null) {
            Map<String, WebSocketSession> sessions = videoCallWebSocketHandler.getSessions();
            WebSocketSession callerSession = sessions.get(call.getCaller());
            WebSocketSession calleeSession = sessions.get(call.getCallee());

            try {
                if (callerSession != null && callerSession.isOpen()) {
                    callerSession.sendMessage(new TextMessage("Call ended"));
                }
                if (calleeSession != null && calleeSession.isOpen()) {
                    calleeSession.sendMessage(new TextMessage("Call ended"));
                }
                return ResponseEntity.ok("Call ended between " + request.getCaller() + " and " + request.getCallee());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to notify participants");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No ongoing call found");
        }

    }

    @PostMapping("/signal")
    public ResponseEntity<String> handleSignal(@RequestBody SignalRequest signalRequest) {
        Map<String, WebSocketSession> sessions = videoCallWebSocketHandler.getSessions();
        WebSocketSession recipientSession = sessions.get(signalRequest.getRecipientId());

        if (recipientSession != null && recipientSession.isOpen()) {
            try {
                recipientSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(signalRequest)));
                return ResponseEntity.ok("Signal processed");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send signal");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient not connected");
        }
    }
}
