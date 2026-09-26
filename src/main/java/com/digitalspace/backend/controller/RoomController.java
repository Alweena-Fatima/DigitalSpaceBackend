package com.digitalspace.backend.controller;


import com.digitalspace.backend.dto.*;
import com.digitalspace.backend.entity.MemberStatus;
import com.digitalspace.backend.entity.Room;
import com.digitalspace.backend.entity.RoomMember;
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
    //now endpoint for goal creation
    @PostMapping("/{roomCode}/goals")
    public GoalResponseDTO createGoal(@PathVariable String roomCode, @RequestBody GoalRequestDTO goaltitle){
        // Save the goal in the database
        GoalResponseDTO createdGoal =
                roomService.createGoal(
                        roomCode,
                        goaltitle.getTitle()
                );

        // Tell everyone in this room about the new goal
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
    //now get all goal endpoint
    @GetMapping("/{roomCode}/goals")
    public List<GoalResponseDTO> getGoals(@PathVariable String roomCode){
        return roomService.getGoals(roomCode);
    }
    //now put endpoint to update the goal status
    @PutMapping("/goals/{goalId}")
    public GoalResponseDTO updateGoal(
            @PathVariable("goalId") Long goalId,
            @RequestBody GoalUpdateRequestDTO request
    ) {

        // Update the goal in the database
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

        // Delete the goal and get its room code
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
    //now Create word apis endpoint
    @PostMapping("/{roomCode}/words")
    public WordResponseDTO createWord(@PathVariable String roomCode, @RequestBody WordRequestDTO req){
        return roomService.createWord(roomCode, req.getWord(), req.getMeaning());
    }
    //now endpoint to get all saved words in the room
    @GetMapping("/{roomCode}/words")
    public List<WordResponseDTO> getWords(@PathVariable String roomCode ){
        return roomService.getWords(roomCode);
    }
    //now delete the word endpoint
    @DeleteMapping("/words/{wordId}")
    public void deleteWord(
            @PathVariable("wordId") Long wordId
    ) {
        roomService.deleteWord(wordId);
    }
    //now quote creation endpoint
    @PostMapping("/{roomCode}/quotes")
    public QuoteResponseDTO createQuote(@PathVariable String roomCode, @RequestBody QuoteRequestDTO req){
        return roomService.createQuote(
                roomCode,
                req.getQuote(),
                req.getAuthor()
        );
    }
    //get all quote endpoint
    @GetMapping("/{roomCode}/quotes")
    public List<QuoteResponseDTO> getQuotes( @PathVariable String roomCode) {
        return roomService.getQuotes(roomCode);
    }
    //delete the quote
    @DeleteMapping("/quotes/{quoteId}")
    public void deleteQuote(@PathVariable("quoteId") Long quoteId){
        roomService.deleteQuote(quoteId);
    }
    @PutMapping("/{roomCode}/theme")
    public RoomResponseDTO updateTheme(
            @PathVariable String roomCode,
            @RequestParam RoomTheme theme
    ) {
        return roomService.updateTheme(roomCode, theme);
    }
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
    @DeleteMapping("/{roomCode}/members/{memberId}")
    public void leaveRoom(
            @PathVariable String roomCode,
            @PathVariable Long memberId
    ) {
        roomService.leaveRoom(roomCode, memberId);
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
