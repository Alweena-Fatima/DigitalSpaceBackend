package com.digitalspace.backend.mapper;

import com.digitalspace.backend.dto.RoomMemberResponseDTO;
import com.digitalspace.backend.entity.RoomMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMemberMapper {

    RoomMemberResponseDTO toResponseDTO(RoomMember roomMember);
}