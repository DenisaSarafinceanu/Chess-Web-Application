package com.example.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ChessMove {
    private String sourceSquare;
    private String targetSquare;

    public String getSourceSquare() {
        return sourceSquare;
    }

    public String getTargetSquare() {
        return targetSquare;
    }

    public void setSourceSquare(String sourceSquare) {
        this.sourceSquare = sourceSquare;
    }

    public void setTargetSquare(String targetSquare) {
        this.targetSquare = targetSquare;
    }
}
