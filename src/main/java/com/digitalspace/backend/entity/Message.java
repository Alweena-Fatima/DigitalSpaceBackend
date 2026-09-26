package com.digitalspace.backend.entity;

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
    //Which room this message belongs to.
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    //who send the message
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private RoomMember member;

    //what is the message
    @Column(nullable = false, length = 200)
    private String content;

    //time of the message
    @Column(nullable = false)
    private LocalDateTime sentAt;
}