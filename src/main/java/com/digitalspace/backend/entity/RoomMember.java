package com.digitalspace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMember {

    // Unique ID for each room member
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MANY members can belong to ONE room
    // Example: Alweena, Sara, and John can all belong to Room ABC123 all will have
    // same room_id : 10
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Nickname that the member uses inside the room
    @Column(nullable = false, length = 50)
    private String nickname;

    // Current status of the member
    // Example: ACTIVE, LEFT, etc.
    //Room: ABC123
    //
    //Members:
    // ├── Alweena → STUDYING
    // ├── Sarah   → READING
    // └── Ali     → BREAK
    @Column(nullable = false, length = 50)
    private String displayName;
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    // Date and time when the member joined the room
    private LocalDateTime joinedAt;
}