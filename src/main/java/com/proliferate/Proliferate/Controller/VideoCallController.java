package com.proliferate.Proliferate.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proliferate.Proliferate.Domain.DTO.Chat.CallRequest;
import com.proliferate.Proliferate.Domain.DTO.Chat.SignalRequest;

import com.proliferate.Proliferate.ExeceptionHandler.UserAlreadyExistsException;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.UserService;
import com.proliferate.Proliferate.config.VideoCallWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final VideoCallWebSocketHandler videoCallWebSocketHandler;

    private Map<String, CallRequest> ongoingCalls = new HashMap<>();

    @PostMapping("/start")
    public String startCall(@RequestBody CallRequest request) {
    // Logic to start a video call
    // Validate the caller and callee
        if (request.getCaller() == null || request.getCallee() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Caller and Callee must be specified");
        }
        if(studentRepository.existsByUserName(request.getCaller()) || tutorRepository.existsByEmail(request.getCaller())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is no account with this username.");
        }

        if(studentRepository.existsByUserName(request.getCallee()) || tutorRepository.existsByEmail(request.getCallee())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is no account with this username.");
        }

        // Notify the callee of the incoming call
        Map<String, WebSocketSession> sessions = videoCallWebSocketHandler.getSessions();
        WebSocketSession calleeSession = sessions.get(request.getCallee());
        
        if (calleeSession != null && calleeSession.isOpen()) {
            CallRequest call = new CallRequest(request.getCaller(), request.getCallee());
            ongoingCalls.put(request.getCaller(), call);

            try {
                calleeSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(call)));
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to notify callee");
            }

            return "Call started between " + request.getCaller() + " and " + request.getCallee();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Callee not connected");
        }
}


    @PostMapping("/end")
    public String endCall(@RequestBody CallRequest request) {
         // Validate the caller and callee
        if (request.getCaller() == null || request.getCallee() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Caller and Callee must be specified");
        }
        if(studentRepository.existsByUserName(request.getCaller()) || tutorRepository.existsByEmail(request.getCaller())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no account with this detail.");
        }
        if(studentRepository.existsByUserName(request.getCallee()) || tutorRepository.existsByEmail(request.getCallee())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no account with this detail.");
        }

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
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to notify participants");
            }

            return "Call ended between " + request.getCaller() + " and " + request.getCallee();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No ongoing call found");
        }
    }

    @PostMapping("/signal")
    public String handleSignal(@RequestBody SignalRequest signalRequest)  throws Exception {
        // Logic to handle signaling messages (offer, answer, ICE candidates)
     Map<String, WebSocketSession> sessions = videoCallWebSocketHandler.getSessions();

        // Find the recipient session
        WebSocketSession recipientSession = sessions.get(signalRequest.getRecipientId());

        if (recipientSession != null && recipientSession.isOpen()) {
            // Forward the message to the recipient via WebSocket
            recipientSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(signalRequest)));
            return "Signal processed";
        } else {
            return "Recipient not connected";
        }
    }

}