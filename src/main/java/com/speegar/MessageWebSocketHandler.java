package com.speegar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageWebSocketHandler.class);
    
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final Map<WebSocketSession, Set<String>> sessionTopics = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        sessionTopics.put(session, new CopyOnWriteArraySet<>());
        logger.info("WebSocket connection established: {}", session.getId());
        
        // Send connection confirmation
        Map<String, Object> message = Map.of(
            "type", "CONNECTION",
            "status", "CONNECTED",
            "sessionId", session.getId()
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Received message: {}", payload);
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> data = objectMapper.readValue(payload, Map.class);
            
            if ("SUBSCRIBE".equals(data.get("type"))) {
                String topic = data.get("topic");
                sessionTopics.get(session).add(topic);
                logger.info("Session {} subscribed to topic: {}", session.getId(), topic);
            }
        } catch (Exception e) {
            logger.error("Error handling message", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionTopics.remove(session);
        logger.info("WebSocket connection closed: {}", session.getId());
    }

    public void broadcastMessage(String topic, Object payload) {
        Map<String, Object> message = Map.of(
            "topic", topic,
            "payload", payload
        );
        
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            TextMessage textMessage = new TextMessage(jsonMessage);
            
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        logger.error("Error sending message to session {}", session.getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error broadcasting message", e);
        }
    }
}