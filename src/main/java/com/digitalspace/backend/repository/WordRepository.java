package com.digitalspace.backend.repository;

import com.digitalspace.backend.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findByRoomId(Long roomId);
}