package com.digitalspace.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuoteResponseDTO {

    private Long id;
    private String quote;
    private String author;
    private LocalDateTime createdAt;
}