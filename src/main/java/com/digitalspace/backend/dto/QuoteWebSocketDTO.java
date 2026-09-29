package com.digitalspace.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuoteWebSocketDTO {

    private String action;
    private String roomCode;
    private QuoteResponseDTO quote;
    private Long quoteId;

    public QuoteWebSocketDTO() {
    }

    // CREATE
    public QuoteWebSocketDTO(
            String action,
            String roomCode,
            QuoteResponseDTO quote) {

        this.action = action;
        this.roomCode = roomCode;
        this.quote = quote;
    }

    // DELETE
    public QuoteWebSocketDTO(
            String action,
            String roomCode,
            Long quoteId) {

        this.action = action;
        this.roomCode = roomCode;
        this.quoteId = quoteId;
    }
}