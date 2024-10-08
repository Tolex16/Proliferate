package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.ChatThread;
import com.proliferate.Proliferate.Domain.Entities.Message;
import com.proliferate.Proliferate.Domain.Entities.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
    Optional<ChatThread> findByThreadId(String threadId);

    List<ChatThread> findAllByStudent_StudentId(Long studentId);
    
    List<ChatThread> findAllByTutor_TutorId(Long tutorId);

    void deleteByThreadId(String threadId);
}