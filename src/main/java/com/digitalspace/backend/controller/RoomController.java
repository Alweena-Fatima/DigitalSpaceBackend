package com.digitalspace.backend.controller;


import com.digitalspace.backend.dto.*;
import com.digitalspace.backend.entity.MemberStatus;
import com.digitalspace.backend.entity.RoomTheme;
import com.digitalspace.backend.service.MessageService;
import com.digitalspace.backend.service.RoomService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:5173")
public class RoomController {
    private final RoomService roomService;
    //add msg service to get all the msg after refresh too
    private final MessageService msgService;
    private final SimpMessagingTemplate messagingTemplate;

    public RoomController(RoomService roomService, MessageService msgService, SimpMessagingTemplate messagingTemplate) {
        this.roomService = roomService;
        this.msgService = msgService;
        this.messagingTemplate = messagingTemplate;
    }
    // =========================================================
    // ROOM APIs
    // =========================================================

    //create a new room
    @PostMapping
    public RoomResponseDTO createRoom(){

        return roomService.createRoom();
    }

    //join the existing room using roomCode

    @PostMapping("/join")
    public RoomMemberResponseDTO joinRoom(
            @RequestBody RoomJoinRequestDTO req
    ) {
        // First save the new member to the database.
        RoomMemberResponseDTO newMember =
                roomService.joinRoom(req);

        // Tell everyone already connected to this room
        // that a new member has joined.
        messagingTemplate.convertAndSend(
                "/topic/room/" + req.getRoomCode(),
                new MemberEventDTO(
                        "JOIN",
                        newMember.getId(),
                        newMember.getDisplayName(),
                        newMember.getStatus().name()
                )
        );

        // Return the newly created member to the user who joined.
        return newMember;
    }



    //get the list of all member currently in the room
    @GetMapping("/{roomCode}/members")
    public List<RoomMemberResponseDTO> getRoomMembers(@PathVariable String roomCode){
        return roomService.getRoomMembers(roomCode);
    }

    //get the room info
    @GetMapping("/{roomCode}")
    public RoomResponseDTO getRoom(@PathVariable String roomCode){
        return roomService.getRoom(roomCode);
    }
    // =========================================================
    // GOAL APIs
    // =========================================================
    @PostMapping("/{roomCode}/goals")
    public GoalResponseDTO createGoal(@PathVariable String roomCode, @RequestBody GoalRequestDTO goaltitle){
        // Save the goal first so we get its generated ID and complete data.
        GoalResponseDTO createdGoal =
                roomService.createGoal(
                        roomCode,
                        goaltitle.getTitle()
                );

        // Broadcast the new goal so everyone in the room sees it instantly.
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode + "/goals",
                new GoalWebSocketDTO(
                        "CREATE",
                        roomCode,
                        createdGoal
                )
        );

        return createdGoal;
    }
    //Load all the goals of the room
    @GetMapping("/{roomCode}/goals")
    public List<GoalResponseDTO> getGoals(@PathVariable String roomCode){
        return roomService.getGoals(roomCode);
    }
    //now put endpoint to update the goal done status
    @PutMapping("/goals/{goalId}")
    public GoalResponseDTO updateGoal(
            @PathVariable("goalId") Long goalId,
            @RequestBody GoalUpdateRequestDTO request
    ) {

        // Update the goal and get its room information for the WebSocket topic.
        GoalUpdateResult result =
                roomService.updateGoal(goalId, request);

        // Tell everyone in this room about the update
        messagingTemplate.convertAndSend(
                "/topic/room/"
                        + result.getRoomCode()
                        + "/goals",

                new GoalWebSocketDTO(
                        "UPDATE",
                        result.getRoomCode(),
                        result.getGoal()
                )
        );

        // Return the updated goal to the user who made the request
        return result.getGoal();
    }
    //now goal delete end point
    @DeleteMapping("/goals/{goalId}")
    public void deleteGoal(
            @PathVariable("goalId") Long goalId) {

        // Delete the goal and return its room code so we know where to broadcast.
        String roomCode =
                roomService.deleteGoal(goalId);

        // Tell everyone in this room that the goal was deleted
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode + "/goals",

                new GoalWebSocketDTO(
                        "DELETE",
                        roomCode,
                        goalId
                )
        );
    }
    // =========================================================
    // WORD APIs
    // =========================================================

    @PostMapping("/{roomCode}/words")
    public WordResponseDTO createWord(
            @PathVariable String roomCode,
            @RequestBody WordRequestDTO req) {

        // Save the word in the database
        WordResponseDTO createdWord =
                roomService.createWord(
                        roomCode,
                        req.getWord(),
                        req.getMeaning()
                );

        // Tell everyone in this room about the new word
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode + "/words",

                new WordWebSocketDTO(
                        "CREATE",
                        roomCode,
                        createdWord
                )
        );

        return createdWord;
    }
    //Load all the words of room


    @GetMapping("/{roomCode}/words")
    public List<WordResponseDTO> getWords(
            @PathVariable String roomCode) {

        return roomService.getWords(roomCode);
    }

    //delete word

    @DeleteMapping("/words/{wordId}")
    public void deleteWord(
            @PathVariable("wordId") Long wordId) {

        // Delete the word and get its room code
        String roomCode =
                roomService.deleteWord(wordId);

        // Tell everyone in this room that the word was deleted
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode + "/words",

                new WordWebSocketDTO(
                        "DELETE",
                        roomCode,
                        wordId
                )
        );
    }
    // =========================================================
    // QUOTE APIs
    // =========================================================
    @PostMapping("/{roomCode}/quotes")
    public QuoteResponseDTO createQuote(
            @PathVariable String roomCode,
            @RequestBody QuoteRequestDTO req) {

        // Save the quote in the database
        QuoteResponseDTO createdQuote =
                roomService.createQuote(
                        roomCode,
                        req.getQuote(),
                        req.getAuthor()
                );

        // Tell everyone in this room about the new quote
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode + "/quotes",

                new QuoteWebSocketDTO(
                        "CREATE",
                        roomCode,
                        createdQuote
                )
        );

        return createdQuote;
    }

    //get all quote endpoint
    @GetMapping("/{roomCode}/quotes")
    public List<QuoteResponseDTO> getQuotes( @PathVariable String roomCode) {
        return roomService.getQuotes(roomCode);
    }

    //delete the quote
    @DeleteMapping("/quotes/{quoteId}")
    public void deleteQuote(
            @PathVariable("quoteId") Long quoteId) {

        // Delete the quote and get its room code
        String roomCode =
                roomService.deleteQuote(quoteId);

        // Tell everyone in this room that the quote was deleted
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode + "/quotes",

                new QuoteWebSocketDTO(
                        "DELETE",
                        roomCode,
                        quoteId
                )
        );
    }

    // =========================================================
    // THEME API
    // =========================================================

    @PutMapping("/{roomCode}/theme")
    public RoomResponseDTO updateTheme(
            @PathVariable String roomCode,
            @RequestParam RoomTheme theme
    ) {

        // Theme is stored at room level, so changing it affects everyone.
        RoomResponseDTO updatedRoom =
                roomService.updateTheme(
                        roomCode,
                        theme
                );

        // Tell everyone in this room about the theme change
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode + "/theme",
                updatedRoom
        );

        return updatedRoom;
    }
    // =========================================================
    // MEMBER APIs
    // =========================================================

    @PutMapping("/{roomCode}/members/{memberId}/status")
    public RoomMemberResponseDTO updateMemberStatus(
            @PathVariable("roomCode") String roomCode,
            @PathVariable("memberId") Long memberId,
            @RequestParam MemberStatus status
    ) {
        return roomService.updateMemberStatus(
                roomCode,
                memberId,
                status
        );
    }

    //api for user to leave the room

    @DeleteMapping("/{roomCode}/members/{memberId}")
    public void leaveRoom(
            @PathVariable String roomCode,
            @PathVariable Long memberId
    ) {
        RoomMemberResponseDTO leftMember =
                roomService.leaveRoom(roomCode, memberId);

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode,
                new MemberLeaveDTO(
                        "LEAVE",
                        leftMember.getId(),
                        leftMember.getDisplayName()
                )
        );
    }


    //add get msg api (to get the old msg of the room)
    //WebSocket → handles new real-time messages
    //MySQL → stores them
    //But React still has no way to ask, “give me the old messages.”
    @GetMapping("/{roomCode}/messages")
    public List<ChatMessageDTO> getMessages(@PathVariable String roomCode){
        return msgService.getMessages(roomCode);
    }
//                      ┌── WebSocket ──→ New messages
//    React ────────────┤
//                      └── REST ───────→ Old messages
//                         ↓
//                        MySQL

}
