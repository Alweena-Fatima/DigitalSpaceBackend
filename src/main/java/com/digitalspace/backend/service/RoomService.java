package com.digitalspace.backend.service;

import com.digitalspace.backend.dto.RoomJoinRequestDTO;
import com.digitalspace.backend.dto.RoomMemberResponseDTO;
import com.digitalspace.backend.dto.RoomResponseDTO;
import com.digitalspace.backend.entity.MemberStatus;
import com.digitalspace.backend.entity.Room;
import com.digitalspace.backend.entity.RoomMember;
import com.digitalspace.backend.entity.RoomTheme;
import com.digitalspace.backend.mapper.RoomMapper;
import com.digitalspace.backend.mapper.RoomMemberMapper;
import com.digitalspace.backend.repository.RoomMemberRepository;
import com.digitalspace.backend.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service //this class will handle buisness logic
public class RoomService {
    private final RoomRepository roomrepo; //inject repo
    private final RoomMapper roomMapper;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomMemberMapper roomMemberMapper;
    public RoomService(RoomRepository roomrepo, RoomMapper roomMapper, RoomMemberRepository roomMemberRepository, RoomMemberMapper roomMemberMapper){
        this.roomrepo=roomrepo;
        this.roomMapper = roomMapper;
        this.roomMemberRepository = roomMemberRepository;
        this.roomMemberMapper = roomMemberMapper;
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
    public RoomMemberResponseDTO joinRoom(RoomJoinRequestDTO request){
        //get room info like id from roomcode
        Room room=roomrepo.findByRoomCode(request.getRoomCode()).orElseThrow(
                ()-> new RuntimeException("Room not found")
        );
        // join a member
        RoomMember member = RoomMember.builder()
                .room(room)
                .nickname(request.getNickname())
                .status(MemberStatus.STUDYING) //join karte waqt to studying hi hoga
                .joinedAt(LocalDateTime.now())
                .build();
        RoomMember savedMember = roomMemberRepository.save(member);

        return roomMemberMapper.toResponseDTO(savedMember);


    }
    //get all room members
    public List<RoomMemberResponseDTO> getRoomMembers(String roomCode){
        Room room=roomrepo.findByRoomCode(roomCode).orElseThrow(()-> new RuntimeException("Room does not exist"));
        List<RoomMember> list=roomMemberRepository.findByRoomId(room.getId()); //we will get the member list
        return list.stream()
                .map(roomMemberMapper:: toResponseDTO)//map each entity to response dto
                .toList();

    }
    //now get the room info
    public RoomResponseDTO getRoom(String roomCode){
        Room room=roomrepo.findByRoomCode(roomCode).orElseThrow(()-> new RuntimeException("Room doesnot exist"));
        return roomMapper.toResponseDTO(room);
    }
}
