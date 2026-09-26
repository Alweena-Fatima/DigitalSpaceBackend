package com.digitalspace.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoalWebSocketDTO {

    // What happened to the goal?
    // CREATE, UPDATE, DELETE
    private String action;

    // Room where the change happened
    private String roomCode;

    // Goal data
    private GoalResponseDTO goal;

    // Used when a goal is deleted
    private Long goalId;

    public GoalWebSocketDTO() {
    }

    // For CREATE / UPDATE
    public GoalWebSocketDTO(
            String action,
            String roomCode,
            GoalResponseDTO goal) {

        this.action = action;
        this.roomCode = roomCode;
        this.goal = goal;
    }

    // For DELETE
    public GoalWebSocketDTO(
            String action,
            String roomCode,
            Long goalId) {

        this.action = action;
        this.roomCode = roomCode;
        this.goalId = goalId;
    }
}