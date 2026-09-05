package com.digitalspace.backend.controller;


import com.digitalspace.backend.dto.RoomResponseDTO;
import com.digitalspace.backend.entity.Room;
import com.digitalspace.backend.service.RoomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    @PostMapping
    public RoomResponseDTO createRoom(){
        return roomService.createRoom();
    }
}
