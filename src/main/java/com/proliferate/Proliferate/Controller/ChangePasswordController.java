package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.ChangePasswordRequest;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.ExeceptionHandler.InvalidPasswordException;
import com.proliferate.Proliferate.ExeceptionHandler.SamePasswordException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
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
public class ChangePasswordController {
    @Autowired
    private final ChangePasswordService authenticationService;

    @Autowired
    private final JwtService jwtService;
    private final TutorRepository tutorRepository;

    private final StudentRepository studentRepository;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            Long userId = jwtService.getUserId();  // Use the combined method to get the user ID

            // Check student repository first
            var studentOpt = studentRepository.findById(userId);
            if (studentOpt.isPresent()) {
                var student = studentOpt.get();
                return authenticationService.changeStudentPassword(student, request);
            }

            // Check tutor repository if not found in student repository
            var tutorOpt = tutorRepository.findById(userId);
            if (tutorOpt.isPresent()) {
                var tutor = tutorOpt.get();
                return authenticationService.changeTutorPassword(tutor, request);
            }

            // If neither found, throw an exception
            throw new UserNotFoundException("User Not Found");
        } catch (InvalidPasswordException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (SamePasswordException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}