package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Tutor.*;
import com.proliferate.Proliferate.Response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TutorAuthenticationService {

    ResponseEntity tutorRegister(TutorRegister tutorRegister);
	
    ResponseEntity educationExperience(EducationExperience educationExperience);

    ResponseEntity teachingStyleApproach(TeachingStyleApproach teachingStyleApproach);

    ResponseEntity availabilityPreference(AvailabilityPreference availabilityPreference);
	
	ResponseEntity uploadDocuments(MultipartFile educationalCertificates, MultipartFile resumeCurriculumVitae, MultipartFile professionalDevelopmentCert, MultipartFile identificationDocuments);
	
	ResponseEntity completeRegistration();

    LoginResponse login(LoginTutorRequest loginTutorRequest);

    String checkMail(String email);

}
