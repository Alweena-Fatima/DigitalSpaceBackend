package com.digitalspace.backend.entity;

import com.digitalspace.backend.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Table(name = "goal")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false) //one room can have many goals
    private Room room;

    @Column(nullable = false, length = 200)
    private String title;

    private boolean completed;

    private LocalDateTime createdAt;
}
