package com.example.websocket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class WebSocketUidController {

    // Generate a unique UID for each new connection
    @GetMapping("/generate-websocket-uid")
    public String generateWebSocketUid() {
        String uid = UUID.randomUUID().toString();  // Generate a unique UUID
        return uid;  // Return the generated UID
    }
}
