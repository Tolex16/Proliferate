package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    //List<Message> findByThreadIdAndSenderIdAndReceiverId(String threadId, Long senderId, Long receiverId);

    Optional<Message> findByIdAndThread_ThreadId(Long messageId, String threadId);
    List<Message> findByThread_ThreadId(String threadId);
}