package com.proliferate.Proliferate.ForgotPasswordRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPassTokenRep extends JpaRepository<ForgotPassToken, Long> {
    ForgotPassToken findByToken(String token);
}
