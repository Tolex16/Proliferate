package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.ChatThread;
import com.proliferate.Proliferate.Domain.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
    Optional<ChatThread> findByThreadId(String threadId);
}