package com.digitalspace.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

//The problem is: after updating, the controller needs to know which room this goal belongs to.
//
//So let's return both pieces of information.
@Getter
@AllArgsConstructor
public class GoalUpdateResult {

    private String roomCode;
    private GoalResponseDTO goal;
}