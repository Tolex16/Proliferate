package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.*;
import com.proliferate.Proliferate.Domain.DTO.Student.*;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Service.InviteService;
import com.proliferate.Proliferate.Service.StudentAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final StudentAuthenticationService authenticationService;

    @Autowired
    private final InviteService inviteService;

    @PostMapping("/studentPersonalDetails")
    public ResponseEntity<?> registerPersonalDetails(@Valid @RequestBody StudentRegisterPersDeets studentRegisterPersDeets, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return authenticationService.studentRegister(studentRegisterPersDeets);
    }
	
	@GetMapping("/api/genders")
    public List<String> getGenders() {
        return Arrays.stream(Gender.values())
                     .map(Gender::getDisplayName)
                     .collect(Collectors.toList());
    }

    @PostMapping("/academicDetail")
    public ResponseEntity<?> academicDetail(@Valid @RequestBody AcademicDetail academicDetail, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return authenticationService.academicDetails(academicDetail);
    }

    @PostMapping("/preferences")
    public ResponseEntity<?> preferences(@Valid @RequestBody Preferences preferences, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return authenticationService.preference(preferences);
    }

    @PostMapping("/learningGoals")
    public ResponseEntity<?> learningGoals(@Valid @RequestBody LearningGoals learningGoals, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return authenticationService.learningGoals(learningGoals);
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

	
    @PostMapping("/login")
    public ResponseEntity <LoginResponse> login(@Valid @RequestBody LoginStudentRequest loginRequest, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.OK)
                .header("Location", "/login")
                .body("Logged out successfully");
    }

    @GetMapping(path="/check-mail/{username}")
    public ResponseEntity<?> findUser (@PathVariable String username){

        String checkUsername = authenticationService.checkUsername(username);

        if(checkUsername == null){
            return new ResponseEntity<>(HttpStatus.FOUND);
        }else{
            return new ResponseEntity<>(checkUsername,HttpStatus.FOUND);
        }
    }

    @PostMapping("/friend-invite")
    public ResponseEntity<?> sendFriendInvite(@Valid @RequestBody FriendInvite friendInvite, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return inviteService.friendInvite(friendInvite);
    }
}
