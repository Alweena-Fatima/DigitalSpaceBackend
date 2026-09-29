package com.digitalspace.backend.controller;

// DTOs are used to carry data between React and the backend.
import com.digitalspace.backend.dto.ChatMessageDTO;
import com.digitalspace.backend.dto.StatusMessageDTO;
import com.digitalspace.backend.dto.RoomMemberResponseDTO;

// Enum used to represent a member's current activity.
import com.digitalspace.backend.entity.MemberStatus;

// Services contain the actual business logic.
import com.digitalspace.backend.service.MessageService;
import com.digitalspace.backend.service.RoomService;

// Used to send messages to WebSocket subscribers.
import org.springframework.messaging.simp.SimpMessagingTemplate;

// Used to map incoming WebSocket messages to Java methods.
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.stereotype.Controller;


/*
 * Handles WebSocket communication for the study room.
 *
 * Unlike RoomController, which handles normal HTTP requests,
 * this controller handles real-time messages coming from React.
 */
@Controller
public class WebSocketController {

    // Used to broadcast messages to all users subscribed to a room.
    private final SimpMessagingTemplate messagingTemplate;

    // Handles room/member related business logic.
    private final RoomService roomService;

    // Handles saving and retrieving chat messages.
    private final MessageService messageService;
    public WebSocketController(
            SimpMessagingTemplate messagingTemplate,
            RoomService roomService,
            MessageService messageService
    ) {

        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
        this.messageService = messageService;
    }


    // =========================================================
    // MEMBER STATUS
    // =========================================================

    /*
     * React sends a status update to:
     *
     * /app/status
     *
     * Because "/app" is our application destination prefix,
     * Spring maps this message to the method below.
     */
    @MessageMapping("/status")
    public void updateStatus(StatusMessageDTO message) {

        /*
         * React sends the status as a String.
         *
         * Example:
         * "READING"
         *
         * We convert that String into our Java enum:
         *
         * MemberStatus.READING
         */
        MemberStatus newStatus =
                MemberStatus.valueOf(message.getStatus());


        /*
         * Save the new status in the database.
         *
         * The service also verifies/updates the correct
         * room member using the room code and member ID.
         */
        RoomMemberResponseDTO updatedMember =
                roomService.updateMemberStatus(
                        message.getRoomCode(),
                        message.getMemberId(),
                        newStatus
                );


        /*
         * The database is now updated.
         *
         * Next, we broadcast the updated member information
         * to everyone who is subscribed to this room.
         *
         * Example topic:
         *
         * /topic/room/ABC123
         */
        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomCode(),

                /*
                 * Send only the information the frontend needs
                 * to update the member's status.
                 */
                new StatusMessageDTO(
                        message.getRoomCode(),
                        updatedMember.getId(),
                        updatedMember.getDisplayName(),
                        updatedMember.getStatus().name()
                )
        );
    }


    // =========================================================
    // CHAT
    // =========================================================

    /*
     * React sends a new chat message to:
     *
     * /app/chat
     *
     * Spring routes that message to this method.
     */
    @MessageMapping("/chat")
    public void sendMessage(ChatMessageDTO message) {

        /*
         * Save the message in MySQL first.
         *
         * We save it before broadcasting so that the message
         * gets its database-generated ID and final timestamp.
         */
        ChatMessageDTO savedMessage =
                messageService.saveMessage(
                        message.getRoomCode(),
                        message.getMemberId(),
                        message.getContent()
                );


        /*
         * Now send the saved message to everyone in the room.
         *
         * Example:
         *
         * /topic/room/ABC123/chat
         *
         * Every connected user subscribed to this topic
         * receives the message in real time.
         */
        messagingTemplate.convertAndSend(
                "/topic/room/"
                        + message.getRoomCode()
                        + "/chat",
                savedMessage
        );
    }
}