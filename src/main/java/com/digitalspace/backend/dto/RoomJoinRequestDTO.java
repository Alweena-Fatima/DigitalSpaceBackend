package com.digitalspace.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomJoinRequestDTO {

    private String roomCode;
    private String nickname;
}