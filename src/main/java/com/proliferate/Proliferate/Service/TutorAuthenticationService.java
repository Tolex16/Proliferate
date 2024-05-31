package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Tutor.AvailabilityPreference;
import com.proliferate.Proliferate.Domain.DTO.Tutor.EducationExperience;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TeachingStyleApproach;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorRegister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TutorAuthenticationService {

    ResponseEntity tutorRegister(TutorRegister tutorRegister);
	
    ResponseEntity educationExperience(EducationExperience educationExperience);

    ResponseEntity teachingStyleApproach(TeachingStyleApproach teachingStyleApproach);

    ResponseEntity availabilityPreference(AvailabilityPreference availabilityPreference);
	
	ResponseEntity uploadDocuments(MultipartFile educationalCertificates, MultipartFile resumeCurriculumVitae, MultipartFile professionalDevelopmentCert, MultipartFile identificationDocuments);
	
	ResponseEntity completeRegistration();

    String checkMail(String email);

}
