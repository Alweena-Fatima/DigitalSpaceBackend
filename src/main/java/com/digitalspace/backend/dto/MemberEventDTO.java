package com.digitalspace.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberEventDTO {

    private String action;
    private Long memberId;
    private String displayName;
    private String status;
}

