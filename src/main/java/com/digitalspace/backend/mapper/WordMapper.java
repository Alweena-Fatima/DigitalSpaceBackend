package com.digitalspace.backend.mapper;

import com.digitalspace.backend.dto.WordResponseDTO;
import com.digitalspace.backend.entity.Word;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WordMapper {

    WordResponseDTO toResponseDTO(Word word);
}