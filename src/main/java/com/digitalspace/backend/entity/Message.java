
        package com.digitalspace.backend.entity;

import com.digitalspace.backend.entity.Room;
import com.digitalspace.backend.entity.RoomMember;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The room keeps the message in the room's chat history.
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // A member can leave while their old messages remain.
    @ManyToOne
    @JoinColumn(name = "member_id")
    private RoomMember member;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false)
    private LocalDateTime sentAt;
}

