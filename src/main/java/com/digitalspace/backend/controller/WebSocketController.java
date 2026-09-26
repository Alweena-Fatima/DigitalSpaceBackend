package com.digitalspace.backend.controller;

import com.digitalspace.backend.dto.ChatMessageDTO;
import com.digitalspace.backend.dto.StatusMessageDTO;
import com.digitalspace.backend.dto.RoomMemberResponseDTO;
import com.digitalspace.backend.entity.MemberStatus;
import com.digitalspace.backend.service.MessageService;
import com.digitalspace.backend.service.RoomService;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/*
 * Handles messages coming through WebSocket.
 */
@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    //add message service class
    private final MessageService msgService;
    public WebSocketController(
            SimpMessagingTemplate messagingTemplate,
            RoomService roomService, MessageService msgService) {

        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
        this.msgService = msgService;
    }

    /*
     * React sends status updates to:
     *
     * /app/status
     */
    @MessageMapping("/status")
    public void updateStatus(StatusMessageDTO message) {

        /*
         * Convert the String status coming from React
         * into our MemberStatus enum.
         *
         * Example:
         *
         * "READING" → MemberStatus.READING
         */
        MemberStatus newStatus =
                MemberStatus.valueOf(message.getStatus());

        /*
         * First save the new status to MySQL.
         */
        RoomMemberResponseDTO updatedMember =
                roomService.updateMemberStatus(
                        message.getRoomCode(),
                        message.getMemberId(),
                        newStatus
                );

        /*
         * Broadcast the updated member to everyone
         * connected to this room.
         */
        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomCode(),
                new StatusMessageDTO(
                        message.getRoomCode(),
                        updatedMember.getId(),
                        updatedMember.getDisplayName(),
                        updatedMember.getStatus().name()
                )
        );

        /*
         * Print confirmation in Spring Boot console.
         */
        System.out.println(
                "Status saved and broadcast:" +updatedMember.getDisplayName() + updatedMember.getStatus()
        );

    }
    //save the message api
    @MessageMapping("/chat")
    public void sendMessage(ChatMessageDTO msg){
        //save this message in mysql
        ChatMessageDTO savedMsg=msgService.saveMessage(
                msg.getRoomCode(),
                msg.getMemberId(),
                msg.getContent()

        );
        //send the saved msg to everyone connected in the room
        messagingTemplate.convertAndSend(
                "/topic/room/"
                        + msg.getRoomCode()
                        + "/chat",
                savedMsg
        );
    }
//    @MessageExceptionHandler
//    @SendToUser("/queue/errors")
//    public String handleException(Exception exception) {
//
//        return exception.getMessage();
//    }
}