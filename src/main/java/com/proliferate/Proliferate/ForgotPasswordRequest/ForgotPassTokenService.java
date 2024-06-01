package com.proliferate.Proliferate.ForgotPasswordRequest;

import com.proliferate.Proliferate.Domain.DTO.ResetPassword;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

public interface ForgotPassTokenService {
    @Transactional
    void initiateForgotPass(String email);

    @Transactional
    ResponseEntity<String> resetPassword(ResetPassword resetPassword);
}
