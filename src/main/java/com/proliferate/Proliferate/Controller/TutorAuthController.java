package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Tutor.*;
import com.proliferate.Proliferate.ExeceptionHandler.UserAlreadyExistsException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UsernameNotFoundException;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Service.TutorAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
        } catch (UserAlreadyExistsException ex){
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

    @GetMapping(path="/check-email")
    public ResponseEntity<?> findUser (@Valid @RequestBody EmailVerification emailVerification){


        try {
            String email = authenticationService.checkMail(emailVerification);

            if(email == null){
                return new ResponseEntity<>(HttpStatus.FOUND);
            }else{
                return new ResponseEntity<>(true,HttpStatus.FOUND);
            }
        } catch (UsernameNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/update-tutor")
    public ResponseEntity<?> updateTutor(@RequestPart("updateTutor") UpdateTutor updateTutor,
                                         @RequestPart("studentImage") MultipartFile studentImage, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        try {
            authenticationService.updateTutor(updateTutor,studentImage);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (UserNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

		
//    @PostMapping(path = " e", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity addRecipe(@ModelAttribute RecipeOperationsDto recipeDto, @RequestPart("file") MultipartFile file) {
//        // handle the file attachment
//        if (file != null) {
//            recipeDto.setFeaturedImage(file);
//        }
//        return new ResponseEntity(recipeService.addRecipe(recipeDto), HttpStatus.CREATED);
//    }

}
