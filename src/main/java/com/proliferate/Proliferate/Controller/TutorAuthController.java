package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Tutor.*;
import com.proliferate.Proliferate.Domain.DTO.Verify2FARequest;
import com.proliferate.Proliferate.ExeceptionHandler.*;
import com.proliferate.Proliferate.Service.TutorAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1/authorize")
@RequiredArgsConstructor
public class TutorAuthController {

    @Autowired
    private final TutorAuthenticationService authenticationService;

    @PostMapping("/tutorPersonalDetails")
    public ResponseEntity<?> tutorPersonalDetails(@Valid @RequestBody TutorRegister tutorRegister, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        try {
            var tutorStore = authenticationService.tutorRegister(tutorRegister);
            return new ResponseEntity<>(tutorStore, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException | StudentEmailPresentException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PostMapping("/educationExperience")
    public ResponseEntity<?> educationExperience(@Valid @RequestBody EducationExperience educationExperience, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        try {
            authenticationService.educationExperience(educationExperience);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

    }

    @PostMapping("/teachingStyleApproach")
    public ResponseEntity<?> teachingStyleApproach(@Valid @RequestBody TeachingStyleApproach teachingStyleApproach, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        try {
            authenticationService.teachingStyleApproach(teachingStyleApproach);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/availabilityPreference")
    public ResponseEntity<?> availabilityPreference(@Valid @RequestBody AvailabilityPreference availabilityPreference, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        try {
            authenticationService.availabilityPreference(availabilityPreference);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
	
	@PostMapping("/upload-documents")
    public ResponseEntity<?> uploadDocuments(@RequestParam("educationalCertificates") MultipartFile educationalCertificates,
                                         @RequestParam("resumeCurriculumVitae") MultipartFile resumeCurriculumVitae,
                                         @RequestParam("professionalDevelopmentCert") MultipartFile professionalDevelopmentCert,
                                         @RequestParam("identificationDocuments") MultipartFile identificationDocuments) throws IOException {

        try {
            authenticationService.uploadDocuments(educationalCertificates, resumeCurriculumVitae, professionalDevelopmentCert,identificationDocuments);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

	@Transactional
	@PostMapping("/tutorCompleteRegistration")
	public ResponseEntity<?> completeRegistration() {
		return authenticationService.completeRegistration();
	}

    @PostMapping("/login-tutor")
    public ResponseEntity <?> login(@Valid @RequestBody LoginTutorRequest loginRequest, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        try {
            return new ResponseEntity<>(authenticationService.login(loginRequest), HttpStatus.ACCEPTED);
        }catch (IllegalArgumentException | UserNotFoundException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (AccountNotVerifiedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Boolean>> findUser(@PathVariable String email) {
        try {
            Map<String, Boolean> emailExists = authenticationService.checkMail(email);
            return new ResponseEntity<>(emailExists, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/update-tutor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateTutor(@Valid @ModelAttribute UpdateTutor updateTutor, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            authenticationService.updateTutor(updateTutor);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (UserNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/verify-tutor-2fa")
    public ResponseEntity<?> verifyStudent2FACode(@Valid @RequestBody Verify2FARequest request) {
        return authenticationService.verifyTutor2FACode(request);
    }
}
