package com.digitalspace.backend.repository;
import com.digitalspace.backend.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {
    // Given room ID se us room ke saare members find karte hain.
// Ek room mein multiple members ho sakte hain isliye List return hoti hai.
    List<RoomMember> findByRoomId(Long roomId);
}