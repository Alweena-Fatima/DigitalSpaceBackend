package com.digitalspace.backend.repository;

import com.digitalspace.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // User room code deta hai (e.g. "ABC123").
// Is room code se actual Room entity find hoti hai,
// jisse uska room ID milta hai aur aage RoomMember ko
// us Room ke saath associate kiya jaata hai.
//    User enters room code
//        ↓
//     "ABC123"
//        ↓
//    findByRoomCode("ABC123")
//        ↓
//    Room mil gaya
//        ↓
//    Room ID = 5
//        ↓
//    RoomMember mein room = Room(id=5)
//        ↓
//    Member save
    Optional<Room> findByRoomCode(String roomCode);
}