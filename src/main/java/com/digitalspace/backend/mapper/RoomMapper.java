package com.digitalspace.backend.mapper;

import com.digitalspace.backend.dto.RoomResponseDTO;
import com.digitalspace.backend.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomResponseDTO toResponseDTO(Room room);
}