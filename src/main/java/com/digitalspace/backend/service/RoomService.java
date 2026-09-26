package com.digitalspace.backend.service;

import com.digitalspace.backend.dto.*;
import com.digitalspace.backend.entity.*;
import com.digitalspace.backend.mapper.*;
import com.digitalspace.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
    private final GoalRepository goalRepository;
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
        // Check if the room already has 6 members
        List<RoomMember> members =
                roomMemberRepository.findByRoomId(room.getId());

        if (members.size() >= 6) {
            throw new RuntimeException("Room is full. Maximum 6 members allowed.");
        }
        // Check if the nickname is already used in this room
        boolean nicknameExists = members.stream()
                .anyMatch(member ->
                        member.getNickname().equalsIgnoreCase(request.getNickname())
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
    //now time to create goal logic
    public GoalResponseDTO createGoal(String roomCode, String goalTitle){
        Room room=roomrepo.findByRoomCode(roomCode).orElseThrow(()-> new RuntimeException("Room doesnot exist"));
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
    // now logic to update the goal status
// goalId = 2 → find goal 2 → completed = true → save → return updated DTO
    public GoalUpdateResult updateGoal(
            Long goalId,
            GoalUpdateRequestDTO req) {

        // Find the goal
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

        // Get the room this goal belongs to
        String roomCode =
                goal.getRoom().getRoomCode();

        // Return both goal + room information
        return new GoalUpdateResult(
                roomCode,
                response
        );
    }
    //now logic to delete the goal controller ko delete ke baad pata nahi hoga ki goal kis room ka tha, so WebSocket topic nahi pata chalega.
//    public void deleteGoal(Long goalId) {
//
//        Goal goal = goalRepository.findById(goalId)
//                .orElseThrow(() -> new RuntimeException("Goal not found"));
//
//        goalRepository.delete(goal);
//    }
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
    //now create word
    public WordResponseDTO createWord(String roomCode, String word, String meaning){
        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));
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
        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        List<Word> words=wordRepository.findByRoomId(room.getId());
        return words.stream()
                .map(wordMapper::toResponseDTO)
                .toList();
    }
    //logic to delete the word
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

        List<Quote> quotes = quoteRepo.findByRoomId(room.getId());
        return quotes.stream()
                .map(quoteMapper::toResponseDTO)
                .toList();
    }
    //now delete the quote
    public void deleteQuote(Long quoteId){
        Quote quote = quoteRepo.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        quoteRepo.delete(quote);
    }
    //update theme
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
    public RoomMemberResponseDTO updateMemberStatus(
            String roomCode,
            Long memberId,
            MemberStatus status
    ) {
        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        RoomMember member = roomMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Make sure this member actually belongs to this room
        if (!member.getRoom().getId().equals(room.getId())) {
            throw new RuntimeException("Member does not belong to this room");
        }

        member.setStatus(status);

        RoomMember updatedMember = roomMemberRepository.save(member);

        return roomMemberMapper.toResponseDTO(updatedMember);
    }
    public void leaveRoom(String roomCode, Long memberId) {

        Room room = roomrepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        RoomMember member = roomMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!member.getRoom().getId().equals(room.getId())) {
            throw new RuntimeException("Member does not belong to this room");
        }

        roomMemberRepository.delete(member);
    }

}
