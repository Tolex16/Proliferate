package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.InvitationEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationEmailRepository  extends JpaRepository<InvitationEmail, Long> {
    Optional<InvitationEmail> findByEmail(String email);
}
