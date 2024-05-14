package com.proliferate.Proliferate.ForgotPasswordRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    @Autowired
    private final ForgotPassTokenService forgotPassTokenService;

    @PostMapping("/initiate")
    public ResponseEntity<String> initiateForgotPass(@RequestBody String email){
        forgotPassTokenService.initiateForgotPass(email);
        return ResponseEntity.ok("Forgot Password sequence initiated. Check your email for instructions.");
    }


   @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
    return forgotPassTokenService.resetPassword(token, newPassword);
}

}
