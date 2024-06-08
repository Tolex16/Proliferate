package com.proliferate.Proliferate.ForgotPasswordRequest;

import com.proliferate.Proliferate.Domain.DTO.ResetPassword;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorVerification;
import com.proliferate.Proliferate.ExeceptionHandler.EmailNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.InvalidPasswordException;
import com.proliferate.Proliferate.ExeceptionHandler.InvalidTokenException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    @Autowired
    private final ForgotPassTokenService forgotPassTokenService;

    @PostMapping("/initiate")
    public ResponseEntity<String> initiateForgotPass(@Valid @RequestBody TutorVerification tutorVerification){

        try {
            forgotPassTokenService.initiateForgotPass(tutorVerification);
            return ResponseEntity.ok("Forgot Password sequence initiated. Check your email for instructions.");
        } catch (EmailNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

    }


   @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPassword resetPassword) {

       try {
           return forgotPassTokenService.resetPassword(resetPassword);
       } catch (InvalidTokenException ex){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
       } catch (InvalidPasswordException ex){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
       }
}

}
