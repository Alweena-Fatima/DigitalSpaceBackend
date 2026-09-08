package com.digitalspace.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
//we dont want room here so
public class GoalResponseDTO {
    private Long id;
    private String title;
    private boolean completed;
    private LocalDateTime createdAt;
}
