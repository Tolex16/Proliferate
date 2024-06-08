package com.proliferate.Proliferate.ForgotPasswordRequest;

import com.proliferate.Proliferate.Domain.DTO.ResetPassword;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorVerification;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

public interface ForgotPassTokenService {
    @Transactional
    void initiateForgotPass(TutorVerification tutorVerification);

    @Transactional
    ResponseEntity<String> resetPassword(ResetPassword resetPassword);
}
