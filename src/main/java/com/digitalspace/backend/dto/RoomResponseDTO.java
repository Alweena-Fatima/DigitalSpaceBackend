package com.digitalspace.backend.dto;

import com.digitalspace.backend.entity.RoomTheme;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class RoomResponseDTO {
    private Long id;
    private String roomCode;
    private RoomTheme theme;
    private LocalDateTime createdAt;
}
