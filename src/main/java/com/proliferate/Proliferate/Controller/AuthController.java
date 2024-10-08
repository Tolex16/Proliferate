package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.*;
import com.proliferate.Proliferate.Domain.DTO.Student.*;
import com.proliferate.Proliferate.ExeceptionHandler.*;
import com.proliferate.Proliferate.Service.StudentAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final StudentAuthenticationService authenticationService;

    @PostMapping("/studentPersonalDetails")
    public ResponseEntity<?> registerPersonalDetails(@Valid @RequestBody StudentRegisterPersDeets studentRegisterPersDeets, BindingResult result) {
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            var studentRegister = authenticationService.studentRegister(studentRegisterPersDeets);
            return new ResponseEntity<>(studentRegister, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException | TutorEmailPresentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }

    }

    @GetMapping("/api/genders")
    public List<String> getGenders() {
        return Arrays.stream(Gender.values())
                .map(Gender::getDisplayName)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/preferredTime")
    public List<String> getPreferredTime() {
        return Arrays.stream(PreferredTime.values())
                .map(PreferredTime::getDisplayName)
                .collect(Collectors.toList());
    }

    @PostMapping("/academicDetail")
    public ResponseEntity<?> academicDetail(@Valid @RequestBody AcademicDetail academicDetail, BindingResult result) {
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            authenticationService.academicDetails(academicDetail);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/preferences")
    public ResponseEntity<?> preferences(@Valid @RequestBody Preferences preferences, BindingResult result) {
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            authenticationService.preference(preferences);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/learningGoals")
    public ResponseEntity<?> learningGoals(@Valid @RequestBody LearningGoals learningGoals, BindingResult result) {
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            authenticationService.learningGoals(learningGoals);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

    }

    @GetMapping("/terms-and-conditions")
    public ResponseEntity<String> getTermsAndConditions() {
        String termsAndConditions = authenticationService.getTermsAndConditions();
        return ResponseEntity.ok(termsAndConditions);
    }

    @PostMapping("/student-completeRegistration")
    public ResponseEntity<?> completeRegistration() {
        return authenticationService.completeRegistration();
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        return authenticationService.verifyToken(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginStudentRequest loginRequest, BindingResult result) {
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<>(authenticationService.login(loginRequest), HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException | UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (AccountNotVerifiedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, @RequestHeader("Authorization") String token) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        authenticationService.logout(token.substring(7)); // Remove "Bearer " prefix
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/check-student/{username}/{email}")
    public ResponseEntity<Map<String, Boolean>> findStudent(@PathVariable String username, @PathVariable String email) {
        try {
            Map<String, Boolean> checkStudent = authenticationService.checkStudent(username, email);
            return new ResponseEntity<>(checkStudent, HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            // In case of any unexpected exceptions, return an internal server error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/update-student", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateStudent(@Valid @ModelAttribute UpdateStudent updateStudent, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            ResponseEntity<?> response = authenticationService.updateStudent(updateStudent);
            return response;  // Return response from updateStudent()
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            System.out.println("Error updating student");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update student");
        }
    }
	
    @PostMapping("/verify-student-2fa")
    public ResponseEntity<?> verifyStudent2FACode(@Valid @RequestBody Verify2FARequest request) {
        return authenticationService.verifyStudent2FACode(request);
    }
}