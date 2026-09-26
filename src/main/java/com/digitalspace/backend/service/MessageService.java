package com.digitalspace.backend.service;


import com.digitalspace.backend.dto.ChatMessageDTO;
import com.digitalspace.backend.entity.Room;
import com.digitalspace.backend.entity.Message;
import com.digitalspace.backend.entity.RoomMember;
import com.digitalspace.backend.repository.MessageRepository;
import com.digitalspace.backend.repository.RoomMemberRepository;
import com.digitalspace.backend.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository msgRepo;
    private final RoomMemberRepository roomemRepo;
    private final RoomRepository roomRepo;


    public MessageService(MessageRepository msgRepo, RoomMemberRepository roomemRepo, RoomRepository roomRepo) {
        this.msgRepo = msgRepo;
        this.roomemRepo = roomemRepo;
        this.roomRepo = roomRepo;
    }
    //save new msg
    public ChatMessageDTO saveMessage(String roomCode, Long memberId, String content){
        //find the room by roomcode
        Room room=roomRepo.findByRoomCode(roomCode)
                .orElseThrow(()->
                        new RuntimeException("Room Not Found"));
        //find the member who is sending the msg using memberid
        RoomMember member=roomemRepo.findById(memberId)
                .orElseThrow(()->
                        new RuntimeException("member Not Found"));
        //check if the member belongs to same room
        if(!member.getRoom().getId().equals(room.getId())){
            throw new RuntimeException(
                    "Member does not belong to this room"
            );
        }
        //get the member last message to calculte the 20sec span
        Optional<Message> lastMsg=
                msgRepo.findTopByMemberIdOrderBySentAtDesc(memberId);
        //now logic
        if(lastMsg.isPresent()){
            //get last msg sent at
            LocalDateTime lastSentAt=
                    lastMsg.get().getSentAt();
            LocalDateTime now=LocalDateTime.now();
            //calculate how many seconds have passwed
            long secondPassed = java.time.Duration
                    .between(lastSentAt,now)
                    .getSeconds();
            //if less than 20sec reject
            if(secondPassed<20){
                long secRem=20-secondPassed;
                throw new RuntimeException(
                        "Please Wait" +secRem + "Seconds before sending another message"
                );
            }
        }
        //create message
        Message msg=Message.builder()
                .room(room)
                .member(member)
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();
        //save it in msgrepo
        Message savedMsg=msgRepo.save(msg);
        //convert ENtity to dto
        return convertToDTO(savedMsg);
    }
    //now fetch the all message of a room
    public List<ChatMessageDTO> getMessages(String roomCode){
        Room room=roomRepo.findByRoomCode(roomCode)
                .orElseThrow(()->
                        new RuntimeException("Room Not Found"));
        return msgRepo
                .findByRoomIdOrderBySentAtAsc(room.getId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    //now function to convert entity to dto
    public ChatMessageDTO convertToDTO(Message msg){
        ChatMessageDTO dto=new ChatMessageDTO();
        dto.setId(msg.getId());
        dto.setRoomCode(msg.getRoom().getRoomCode());
        dto.setContent(msg.getContent());
        dto.setMemberId(msg.getMember().getId());
        dto.setUsername(msg.getMember().getDisplayName());
        dto.setSentAt(msg.getSentAt());
        return dto;
    }
}
