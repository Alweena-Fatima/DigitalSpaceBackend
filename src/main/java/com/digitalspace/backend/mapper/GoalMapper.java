package com.digitalspace.backend.mapper;

import com.digitalspace.backend.dto.GoalResponseDTO;
import com.digitalspace.backend.entity.Goal;
import org.mapstruct.Mapper;
// entity --> goalmapper --> goal dto
@Mapper(componentModel = "spring")
public interface GoalMapper {

    GoalResponseDTO toResponseDTO(Goal goal);
}