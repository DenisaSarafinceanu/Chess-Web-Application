package com.example.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChessController {
    @MessageMapping("/chess/{uid}")
    @SendTo("/topic/move/{uid}")
    public ChessMove sendMove(@Payload ChessMove chessMove,  @DestinationVariable String uid) {
        System.out.println("HELO!");
        if (chessMove == null) {
            System.err.println("Received null ChessMove payload!");
            return null; 
        }
        System.out.println("ChessMove received: " + chessMove + "at UID:" + uid);
        return chessMove;
    }
}