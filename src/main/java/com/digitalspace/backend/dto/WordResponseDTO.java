package com.digitalspace.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordResponseDTO {

    private Long id;
    private String word;
    private String meaning;
}