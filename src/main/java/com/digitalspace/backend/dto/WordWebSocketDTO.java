package com.digitalspace.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordWebSocketDTO {

    private String action;
    private String roomCode;
    private WordResponseDTO word;
    private Long wordId;

    public WordWebSocketDTO() {
    }

    // CREATE / UPDATE
    public WordWebSocketDTO(
            String action,
            String roomCode,
            WordResponseDTO word) {

        this.action = action;
        this.roomCode = roomCode;
        this.word = word;
    }

    // DELETE
    public WordWebSocketDTO(
            String action,
            String roomCode,
            Long wordId) {

        this.action = action;
        this.roomCode = roomCode;
        this.wordId = wordId;
    }
}