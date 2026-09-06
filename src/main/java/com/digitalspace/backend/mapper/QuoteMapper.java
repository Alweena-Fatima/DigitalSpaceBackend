package com.digitalspace.backend.mapper;

import com.digitalspace.backend.entity.Quote;
import com.digitalspace.backend.dto.QuoteResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    QuoteResponseDTO toResponseDTO(Quote quote);
}