package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.ChangePasswordRequest;
import com.proliferate.Proliferate.Service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ChangePasswordController {
    @Autowired
    private final AuthenticationService authenticationService;

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody @AuthenticationPrincipal ChangePasswordRequest changePasswordRequest, BindingResult result){
        System.out.println("Has Errors?" + result.hasErrors());
        if (result.hasErrors()){return ResponseEntity.badRequest().body("Invalid input");}

        if (!authenticationService.isCurrentPasswordValid(changePasswordRequest.getCurrentPassword())){
            return ResponseEntity.badRequest().body("Current Password Invalid");
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body("Passwords don't match");
        }
        authenticationService.updatePassword(changePasswordRequest.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
        }
    }
