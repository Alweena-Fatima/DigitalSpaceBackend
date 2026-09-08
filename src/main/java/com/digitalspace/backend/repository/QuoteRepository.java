package com.digitalspace.backend.repository;

import com.digitalspace.backend.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote,Long> {
    List<Quote> findByRoomId(Long roomId);
}
