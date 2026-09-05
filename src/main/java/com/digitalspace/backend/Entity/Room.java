package com.digitalspace.backend.Entity;

import com.digitalspace.backend.Entity.RoomTheme;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String roomCode;
    //we need room theme so that when ever new member join the team room theme should be consistent
    //across all member
    @Enumerated(EnumType.STRING)
    private RoomTheme theme;

    private LocalDateTime createdAt;
}