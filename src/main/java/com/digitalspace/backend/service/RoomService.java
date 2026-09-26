package com.digitalspace.backend.service;

import com.digitalspace.backend.dto.*;
import com.digitalspace.backend.entity.*;
import com.digitalspace.backend.mapper.*;
import com.digitalspace.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service //this class will handle buisness logic
public class RoomService {
    private final RoomRepository roomrepo; //inject repo
    private final RoomMapper roomMapper; //convert the room entities into roomresDTO
    private final RoomMemberRepository roomMemberRepository;//repo use to manage member inside a room
    private final RoomMemberMapper roomMemberMapper;
    private final GoalRepository goalRepository;//manage goal
    private final GoalMapper goalMapper;
    private final WordRepository wordRepository;
    private final WordMapper wordMapper;
    private final QuoteRepository quoteRepo;
    private final QuoteMapper quoteMapper;
    public RoomService(RoomRepository roomrepo, RoomMapper roomMapper, RoomMemberRepository roomMemberRepository, RoomMemberMapper roomMemberMapper, GoalRepository goalRepository, GoalMapper goalMapper, WordRepository wordRepository, WordMapper wordMapper, QuoteRepository quoteRepo, QuoteMapper quoteMapper){
        this.roomrepo=roomrepo;
        this.roomMapper = roomMapper;
        this.roomMemberRepository = roomMemberRepository;
        this.roomMemberMapper = roomMemberMapper;
        this.goalRepository = goalRepository;
        this.goalMapper = goalMapper;
        this.wordRepository = wordRepository;
        this.wordMapper = wordMapper;
        this.quoteRepo = quoteRepo;
        this.quoteMapper = quoteMapper;
    }
    // =========================================================
    // ROOM
    // =========================================================

    //CREATE ROOM LOGIC
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

        Room savedRoom=roomrepo.save(room); //save in repo

        return roomMapper.toResponseDTO(savedRoom);//return dto instead of exposing entity
    }

    //ADDING NEW MEMBER TO EXISTING ROOM
    public RoomMemberResponseDTO joinRoom(RoomJoinRequestDTO request){
        //get room info like id from roomcode
        Room room=roomrepo.findByRoomCode(request.getRoomCode()).orElseThrow(
                ()-> new RuntimeException("Room not found")
        );
        // Check if the room already has 6 members
        List<RoomMember> members =
                roomMemberRepository.findByRoomId(room.getId());

        if (members.size() >= 6) {
            throw new RuntimeException("Room is full. Maximum 6 members allowed.");
        }
        // Check if the nickname is already used in this room
        boolean nicknameExists =
                members.stream()
                        .anyMatch(member ->
                                member.getNickname()
                                        .equalsIgnoreCase(
                                                request.getNickname()
                                        )
                        );

        if (nicknameExists) {
            throw new RuntimeException("Nickname is already taken in this room.");
        }

        // Create the new member
        RoomMember member = RoomMember.builder()
                .room(room)
                .nickname(request.getNickname())
                .displayName(request.getDisplayName())
                .status(MemberStatus.STUDYING)
                .joinedAt(LocalDateTime.now())
                .build();

        // Save the member
        RoomMember savedMember = roomMemberRepository.save(member);

        return roomMemberMapper.toResponseDTO(savedMember);


    }

    //LIST OF ALL MEMBER IN THE ROOM
    public List<RoomMemberResponseDTO> getRoomMembers(String roomCode){

        // Find the room first because members are stored using the room ID.
        Room room=roomrepo.findByRoomCode(roomCode).orElseThrow(()-> new RuntimeException("Room does not exist"));
        //we will get the member list
        List<RoomMember> list=roomMemberRepository.findByRoomId(room.getId());
        //map each entity to response dto
        return list.stream()
                .map(roomMemberMapper:: toResponseDTO)
                .toList();

    }

    //BASIC INFO OF ROOM
    public RoomResponseDTO getRoom(String roomCode){
        // Find the room using the code provided by the frontend.
        Room room=roomrepo.findByRoomCode(roomCode).orElseThrow(()-> new RuntimeException("Room doesnot exist"));
        return roomMapper.toResponseDTO(room);
    }
    // =========================================================
    // GOALS
    // =========================================================

    //now time to create goal logic
    public GoalResponseDTO createGoal(String roomCode, String goalTitle){
        // Find the room that this goal belongs to.
        Room room=roomrepo.findByRoomCode(roomCode).orElseThrow(()-> new RuntimeException("Room doesnot exist"));

        //create a new goal
        Goal goal=Goal.builder()
                .room(room)
                .title(goalTitle)
                .createdAt(LocalDateTime.now())
                .completed(false)
                .build();
        Goal savedGoal=goalRepository.save(goal);
        return goalMapper.toResponseDTO(savedGoal);
    }
    //now get all goal using room id use goal maper to convert goal entity to goalresponse dto and display it
    public List<GoalResponseDTO> getGoals(String roomCode){
        Room room=roomrepo.findByRoomCode(roomCode).orElseThrow(()-> new RuntimeException("Room doesnot exist"));
        List<Goal> goals=goalRepository.findByRoomId(room.getId());
        return goals.stream()
                .map(goalMapper::toResponseDTO)
                .toList();
    }
    // GOAL STATUS
    /*
     * Updates the completion status of an existing goal.
     *
     * We return both the updated goal and its room code because
     * the controller needs the room code to broadcast the change
     * through WebSocket.
     */
// goalId = 2 → find goal 2 → completed = true → save → return updated DTO
    public GoalUpdateResult updateGoal(
            Long goalId,
            GoalUpdateRequestDTO req) {

        // Find the goal that needs to be updated
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() ->
                        new RuntimeException("Goal not found"));

        // Update completion status
        goal.setCompleted(req.isCompleted());

        // Save updated goal
        Goal updatedGoal =
                goalRepository.save(goal);

        // Convert entity to response DTO
        GoalResponseDTO response =
                goalMapper.toResponseDTO(updatedGoal);

        // Get the room so the controller knows where to broadcast the update.
        String roomCode =
                goal.getRoom().getRoomCode();

        // Return both goal + room information
        return new GoalUpdateResult(
                roomCode,
                response
        );
    }
    /*
     * Deletes a goal.
     *
     * The room code is retrieved BEFORE deleting the goal because
     * the controller needs it to know which WebSocket room to notify.
     */
    public String deleteGoal(Long goalId) {

        // Find the goal
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() ->
                        new RuntimeException("Goal not found"));

        // Get the room before deleting the goal
        String roomCode =
                goal.getRoom().getRoomCode();

        // Delete the goal
        goalRepository.delete(goal);

        // Return room code so controller can broadcast
        return roomCode;
    }
    // =========================================================
    // WORDS
    // =========================================================

    //CREATE A NEW WORD
    public WordResponseDTO createWord(String roomCode, String word, String meaning){
        // Find the room where this word should be stored.
        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        //create a word
        Word newWord=Word.builder()
                .room(room)
                .word(word)
                .meaning(meaning)
                .createdAt(LocalDateTime.now())
                .build();
        Word savedWord=wordRepository.save(newWord);
        return wordMapper.toResponseDTO(savedWord);
    }
    //now logic to get all saved word in the room
    public List<WordResponseDTO> getWords(String roomCode){
        // Find the room using its room code.
        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        //get all words of this room
        List<Word> words=wordRepository.findByRoomId(room.getId());

        // Convert each Word entity into a response DTO.
        return words.stream()
                .map(wordMapper::toResponseDTO)
                .toList();
    }
    /*
     * Deletes a word and returns the room code.
     *
     * The room code is needed by the controller to broadcast
     * the deletion to the correct WebSocket topic.
     */
    public String deleteWord(Long wordId) {

        // Find the word
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() ->
                        new RuntimeException("Word not found"));

        // Get the room before deleting the word
        String roomCode =
                word.getRoom().getRoomCode();

        // Delete the word
        wordRepository.delete(word);

        // Return room code so controller can broadcast
        return roomCode;
    }
    // =========================================================
    // QUOTES
    // =========================================================

    //now quote creation logic we need quote author and roomCode
    public QuoteResponseDTO createQuote(String roomCode, String quote, String author){
        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Quote newQuote= Quote.builder()
                .room(room)
                .quote(quote)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();
        Quote savedQuote=quoteRepo.save(newQuote);
        return quoteMapper.toResponseDTO(savedQuote);
    }

    //get the list of all quote
    public List<QuoteResponseDTO> getQuotes(String roomCode) {

        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Fetch all quotes belonging to this room.
        List<Quote> quotes = quoteRepo.findByRoomId(room.getId());
        return quotes.stream()
                .map(quoteMapper::toResponseDTO)
                .toList();
    }
    /*
     * Deletes a quote and returns the room code.
     *
     * The controller needs the room code to broadcast the
     * deletion to everyone in the same room.
     */
    public String deleteQuote(Long quoteId) {

        // Find the quote
        Quote quote = quoteRepo.findById(quoteId)
                .orElseThrow(() ->
                        new RuntimeException("Quote not found"));

        // Get room code before deleting the quote
        String roomCode =
                quote.getRoom().getRoomCode();

        // Delete the quote
        quoteRepo.delete(quote);

        // Return room code so controller knows
        // which room should receive the WebSocket event
        return roomCode;
    }
    // =========================================================
    // THEME
    // =========================================================

    /*
     * Updates the theme of a room.
     *
     * Theme is stored on the Room itself, so it is shared
     * by everyone inside that room.
     */
    public RoomResponseDTO updateTheme(String roomCode, RoomTheme theme) {

        // Find the room using its room code
        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Change the room's theme
        room.setTheme(theme);

        // Save the updated room in the database
        Room updatedRoom = roomrepo.save(room);

        // Return the updated room as DTO
        return roomMapper.toResponseDTO(updatedRoom);
    }
    // =========================================================
    // MEMBER STATUS
    // =========================================================

    /*
     * Updates the current status of a room member.
     *
     * Example:
     * STUDYING → BREAK
     */

    public RoomMemberResponseDTO updateMemberStatus(
            String roomCode,
            Long memberId,
            MemberStatus status
    ) {
        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        // Find the member using their database ID.
        RoomMember member = roomMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Make sure this member actually belongs to this room
        if (!member.getRoom().getId().equals(room.getId())) {
            throw new RuntimeException("Member does not belong to this room");
        }

        //update the member status
        member.setStatus(status);

        RoomMember updatedMember = roomMemberRepository.save(member);

        return roomMemberMapper.toResponseDTO(updatedMember);
    }

    // =========================================================
    // LEAVE ROOM
    // =========================================================
    public void leaveRoom(String roomCode, Long memberId) {

        //find the room

        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        //find the member who wants to leave
        RoomMember member = roomMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!member.getRoom().getId().equals(room.getId())) {
            throw new RuntimeException("Member does not belong to this room");
        }

        roomMemberRepository.delete(member);
    }

}
