package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Tutor.*;
import com.proliferate.Proliferate.Domain.DTO.Verify2FARequest;
import com.proliferate.Proliferate.Response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface TutorAuthenticationService {

    ResponseEntity tutorRegister(TutorRegister tutorRegister);
	
    ResponseEntity educationExperience(EducationExperience educationExperience);

    ResponseEntity teachingStyleApproach(TeachingStyleApproach teachingStyleApproach);

    ResponseEntity availabilityPreference(AvailabilityPreference availabilityPreference);

	ResponseEntity uploadDocuments(MultipartFile educationalCertificates, MultipartFile resumeCurriculumVitae, MultipartFile professionalDevelopmentCert, MultipartFile identificationDocuments);

    @Transactional
	ResponseEntity completeRegistration();

    LoginResponse login(LoginTutorRequest loginTutorRequest);

    Map<String, Boolean> checkMail(String email);
    
	@Transactional
    ResponseEntity updateTutor (UpdateTutor updateTutor);

    ResponseEntity<?> verifyTutor2FACode(Verify2FARequest request);

}
