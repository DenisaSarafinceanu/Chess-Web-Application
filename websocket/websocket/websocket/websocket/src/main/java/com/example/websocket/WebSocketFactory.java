package com.example.websocket;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WebSocketFactory {

    public String createWebSocket(String type) {
        // For demonstration, the factory can decide what to create based on the type.
        // Here we're just generating a new unique ID as an example.
        String uid = UUID.randomUUID().toString();
        
        // You can customize the logic to choose different WebSocket types here based on the 'type'
        // E.g., a WebSocket for games, chats, notifications, etc.
        switch (type) {
            case "game":
                System.out.println("Creating a new WebSocket for Game with UID: " + uid);
                break;
            default:
                System.out.println("Creating a new WebSocket with UID: " + uid);
        }
        
        // Return the unique identifier for this WebSocket
        return uid;
    }
}
