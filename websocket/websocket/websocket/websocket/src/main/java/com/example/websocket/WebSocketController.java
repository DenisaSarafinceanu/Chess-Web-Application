package com.example.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    private final WebSocketConfig webSocketConfig;

    @Autowired
    public WebSocketController(WebSocketConfig webSocketConfig) {
        this.webSocketConfig = webSocketConfig;
    }

    @GetMapping("/create-websocket")
    public String createWebSocket(@RequestParam String type) {
        // Dynamically create a WebSocket of the specified type
        String uid = webSocketConfig.createNewWebSocket(type);
        return "WebSocket created with UID: " + uid;
    }
}
