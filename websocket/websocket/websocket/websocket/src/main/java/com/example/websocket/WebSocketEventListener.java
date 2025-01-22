package com.example.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final AtomicInteger activeUsers = new AtomicInteger(0);
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Check the current number of active users
        if (activeUsers.get() >= 2) {
            logger.info("Connection rejected. Maximum user limit reached.");
            messagingTemplate.convertAndSend("/topic/notifications", "Connection rejected. Maximum user limit of 2 reached.");
            return;
        }
        
        int count = activeUsers.incrementAndGet();
        logger.info("New user connected. Active users: {}", count);
    
        messagingTemplate.convertAndSend("/topic/notifications", "A new user has just connected. Active users: " + count);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        int count = activeUsers.decrementAndGet(); // Decrement the counter
        logger.info("User disconnected. Active users: {}", count);

        messagingTemplate.convertAndSend("/topic/notifications", "A user has disconnected. Active users: " + count);
    }
}