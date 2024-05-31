package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Tutor.AvailabilityPreference;
import com.proliferate.Proliferate.Domain.DTO.Tutor.EducationExperience;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TeachingStyleApproach;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorRegister;
import com.proliferate.Proliferate.Service.TutorAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        return authenticationService.tutorRegister(tutorRegister);
    }
	
	

    @PostMapping("/educationExperience")
    public ResponseEntity<?> educationExperience(@Valid @RequestBody EducationExperience educationExperience, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return authenticationService.educationExperience(educationExperience);
    }

    @PostMapping("/teachingStyleApproach")
    public ResponseEntity<?> teachingStyleApproach(@Valid @RequestBody TeachingStyleApproach teachingStyleApproach, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return authenticationService.teachingStyleApproach(teachingStyleApproach);
    }

    @PostMapping("/availabilityPreference")
    public ResponseEntity<?> availabilityPreference(@Valid @RequestBody AvailabilityPreference availabilityPreference, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        return authenticationService.availabilityPreference(availabilityPreference);
    }
	
	@PostMapping("/upload-documents")
    public ResponseEntity<?> uploadDocuments(@RequestParam("educationalCertificates") MultipartFile educationalCertificates,
                                         @RequestParam("resumeCurriculumVitae") MultipartFile resumeCurriculumVitae,
                                         @RequestParam("professionalDevelopmentCert") MultipartFile professionalDevelopmentCert,
                                         @RequestParam("identificationDocuments") MultipartFile identificationDocuments) throws IOException {
	return authenticationService.uploadDocuments(educationalCertificates, resumeCurriculumVitae, professionalDevelopmentCert,identificationDocuments);
    }
	
	@PostMapping("/tutorCompleteRegistration")
	public ResponseEntity<?> completeRegistration() {
		return authenticationService.completeRegistration();
	}

}
