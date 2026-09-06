package com.digitalspace.backend.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//so that when user tick the goal we will get the req and change the goal status
public class GoalUpdateRequestDTO {
    private boolean completed;
}
