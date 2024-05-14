package com.proliferate.Proliferate.ForgotPasswordRequest;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

public interface ForgotPassTokenService {
    @Transactional
    void initiateForgotPass(String email);

    @Transactional
    ResponseEntity<String> resetPassword(String token, String newPassword);
}
