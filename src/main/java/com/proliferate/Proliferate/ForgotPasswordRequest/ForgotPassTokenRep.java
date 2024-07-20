package com.proliferate.Proliferate.ForgotPasswordRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPassTokenRep extends JpaRepository<ForgotPassToken, Long> {
    Optional<ForgotPassToken> findByToken(String token);
}
