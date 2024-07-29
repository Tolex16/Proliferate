package com.proliferate.Proliferate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proliferate.Proliferate.Domain.DTO.Video.SignalRequest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import java.util.HashMap;
import java.util.Map;

public class VideoCallWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Add session to the map
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle signaling messages (offer, answer, candidate)

        // Parse the incoming message
        SignalRequest signalRequest = objectMapper.readValue(message.getPayload(), SignalRequest.class);

        // Find the recipient session
        WebSocketSession recipientSession = sessions.get(signalRequest.getRecipientId());


        if (recipientSession != null && recipientSession.isOpen()) {
            // Forward the message to the recipient
            recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(signalRequest)));

        }

    }


    @Override
    public void afterConnectionClosed (WebSocketSession session, CloseStatus status) throws Exception {
        // Remove session from the map
        sessions.remove(session.getId());
    }

    public Map<String, WebSocketSession> getSessions () {
        return sessions;
    }
}