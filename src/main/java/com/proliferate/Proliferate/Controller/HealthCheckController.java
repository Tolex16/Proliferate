package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.ChangePasswordRequest;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.ExeceptionHandler.InvalidPasswordException;
import com.proliferate.Proliferate.ExeceptionHandler.SamePasswordException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.AdminRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Service.ChangePasswordService;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.TutorAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthCheckController {
	
    @GetMapping("/health")
     public ResponseEntity<String> healthCheck() {
        return  new ResponseEntity<>("Application is running", HttpStatus.OK);
    }
}