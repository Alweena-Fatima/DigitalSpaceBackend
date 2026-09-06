package com.digitalspace.backend.dto;

import com.digitalspace.backend.entity.MemberStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Getter
@Setter
public class RoomMemberResponseDTO {
    //so front end already know which room member is joining so we dont need that in our response
    private Long id;
    private String nickname;
    private MemberStatus status;
    private LocalDateTime joinedAt;
}
