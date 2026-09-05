package com.digitalspace.backend.service;

import com.digitalspace.backend.dto.RoomResponseDTO;
import com.digitalspace.backend.entity.Room;
import com.digitalspace.backend.entity.RoomTheme;
import com.digitalspace.backend.mapper.RoomMapper;
import com.digitalspace.backend.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service //this class will handle buisness logic
public class RoomService {
    private final RoomRepository roomrepo; //inject repo
    private final RoomMapper roomMapper;
    public RoomService(RoomRepository roomrepo, RoomMapper roomMapper){
        this.roomrepo=roomrepo;
        this.roomMapper = roomMapper;
    }
    //1) generate random room code
    // 2) default room theme
    // 3) createdAt
    // 4) save in repo
    public RoomResponseDTO createRoom(){
        //1
        String roomcode= UUID.randomUUID().toString().substring(0,6).toUpperCase();
        //2
        //builder() is a convenient way to create an object by setting its fields one by one.
        //@Builder automatically generates the builder() method for you.
//        Room room = new Room();
//        room.setRoomCode(roomcode);
//        room.setTheme(RoomTheme.CAFE);
//        room.setCreatedAt(LocalDateTime.now());
        Room room=Room.builder()
                .roomCode(roomcode)
                .createdAt(LocalDateTime.now())
                .theme(RoomTheme.CAFE)
                .build(); //.build() - it convert builder into room object
        Room savedRoom=roomrepo.save(room);
        return roomMapper.toResponseDTO(savedRoom);
    }
}
