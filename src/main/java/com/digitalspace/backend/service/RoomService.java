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
    public GoalResponseDTO updateGoal(Long goalId, GoalUpdateRequestDTO req){
        Goal goal=goalRepository.findById(goalId).orElseThrow(()-> new RuntimeException("Goal not found"));
        goal.setCompleted(req.isCompleted());
        Goal updateGoal=goalRepository.save(goal);
        return goalMapper.toResponseDTO(updateGoal);
    }
    //now logic to delete the goal
    public void deleteGoal(Long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goalRepository.delete(goal);
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
    public void deleteWord(Long wordId){
        Word word=wordRepository.findById(wordId)
                .orElseThrow(()-> new RuntimeException("Word not found"));
        wordRepository.delete(word);
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

}
