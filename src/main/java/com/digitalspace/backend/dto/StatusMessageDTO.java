package com.digitalspace.backend.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * This class represents the message
 * that travels through WebSocket.
 *
 * Example message:
 *
 * {
 *     "memberId": 5,
 *     "displayName": "Alweena",
 *     "status": "READING"
 * }
 */
@Getter
@Setter
public class StatusMessageDTO {

    private String roomCode;
    private Long memberId;
    private String displayName;
    private String status;

    public StatusMessageDTO() {
    }

    public StatusMessageDTO(
            String roomCode,
            Long memberId,
            String displayName,
            String status) {

        this.roomCode = roomCode;
        this.memberId = memberId;
        this.displayName = displayName;
        this.status = status;
    }
}