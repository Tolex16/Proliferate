package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Session;
import com.proliferate.Proliferate.Domain.Entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    long countByTutorTutorId(Long tutorId);

    Optional<Session> findByTutorTutorId(Long tutorId); // Ensure only one result
}
