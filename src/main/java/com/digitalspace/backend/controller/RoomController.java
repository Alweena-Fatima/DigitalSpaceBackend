package com.digitalspace.backend.controller;


import com.digitalspace.backend.dto.RoomJoinRequestDTO;
import com.digitalspace.backend.dto.RoomMemberResponseDTO;
import com.digitalspace.backend.dto.RoomResponseDTO;
import com.digitalspace.backend.entity.Room;
import com.digitalspace.backend.entity.RoomMember;
import com.digitalspace.backend.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/join")
    public RoomMemberResponseDTO joinRoom(@RequestBody RoomJoinRequestDTO req){
        return roomService.joinRoom(req);
    }
    //get the list of all member of room
    @GetMapping("/{roomCode}/members")
    public List<RoomMemberResponseDTO> getRoomMembers(@PathVariable String roomCode){
        return roomService.getRoomMembers(roomCode);
    }
    //get the room info
    @GetMapping("/{roomCode}")
    public RoomResponseDTO getRoom(@PathVariable String roomCode){
        return roomService.getRoom(roomCode);
    }
}
