package com.digitalspace.backend.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
//why cant we send the msg entity
//it contains room obj and room member object but we only want msg display name sent time for our frontend
//message entity -> ChatMessageDto -> frontend
public class ChatMessageDTO {

    private Long id;

    //get the roomcode
    private String roomCode;
    //get the msg
    private String content;

    //get the user name of the person
    private String Username;

    //get the member id of the username
    private Long memberId;

    // When the message was sent
    private LocalDateTime sentAt;


}
