package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Tutor.*;
import com.proliferate.Proliferate.ExeceptionHandler.*;
import com.proliferate.Proliferate.Response.LoginResponse;
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
    public ResponseEntity <LoginResponse> login(@RequestBody LoginTutorRequest loginRequest, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> findUser(@Valid @RequestBody TutorVerification emailVerification) {
        try {
            Map<String, Boolean> emailExists = authenticationService.checkMail(emailVerification);
            return new ResponseEntity<>(emailExists, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/update-tutor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateTutor(@ModelAttribute UpdateTutor updateTutor, BindingResult result) {
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

    @GetMapping("/documents")
    public ResponseEntity<Map<String, byte[]>> getDocuments(
        @RequestParam Long tutorId,
        @RequestParam String documentType) {
        try {
            Map<String, byte[]> documents = authenticationService.getDocuments(tutorId, documentType);

            if (documents.values().stream().allMatch(Objects::isNull)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(documents, headers, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
