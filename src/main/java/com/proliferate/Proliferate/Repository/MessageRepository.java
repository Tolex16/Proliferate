package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    //List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<Message> findByThread_ThreadId(String threadId);
}