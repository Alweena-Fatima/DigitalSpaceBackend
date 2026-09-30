package com.digitalspace.backend.repository;

import com.digitalspace.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message,Long> {

    //get the all msg of the roomm spring will automatically create sql query to fetch that
    //and sort all the msg by its send time
    List<Message> findByRoomIdOrderBySentAtAsc(Long roomId);


    //got the most recent msg sent by any member to calculate 20sec logic
    Optional<Message> findTopByMemberIdOrderBySentAtDesc(Long memberId);


    // Get all messages sent by a particular member
    List<Message> findByMemberId(Long memberId);
}
