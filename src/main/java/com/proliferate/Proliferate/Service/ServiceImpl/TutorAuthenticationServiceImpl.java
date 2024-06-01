package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Tutor.*;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.EmailNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserAlreadyExistsException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Response.PersonDetailsResponse;
import com.proliferate.Proliferate.Service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TutorAuthenticationServiceImpl implements TutorAuthenticationService {
    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final TutorRepository tutorRepository;

    @Autowired
    private final UserService tutorService;
    private final Mapper<TutorEntity, TutorRegister> tutorRegisterMapper;

    private final Mapper<TutorEntity, EducationExperience> educationExperienceMapper;

    private final Mapper<TutorEntity, TeachingStyleApproach> teachingStyleApproachMapper;

    private final Mapper<TutorEntity, AvailabilityPreference> availabilityPreferenceMapper;

    private final Mapper<TutorEntity, UploadDocuments> uploadDocumentsMapper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
    private final EmailService emailService;

    private final Mapper<TutorEntity, TutorDto> tutorMapper;
    @Autowired
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> tutorRegister(TutorRegister tutorRegister){
        if(tutorRepository.existsByUserName(tutorRegister.getUserName())){
            throw new UserAlreadyExistsException("There is an account with this username.");
        }
        if(tutorRepository.existsByEmail(tutorRegister.getEmail())){
            throw new UserAlreadyExistsException("There is an account associated with this email already");
        }
        try {
            tutorRegister.setPassword(passwordEncoder.encode(tutorRegister.getPassword()));
            TutorEntity tutorEntity = tutorRegisterMapper.mapFrom(tutorRegister);
            tutorEntity.setRole(Role.TUTOR);
            tutorRepository.save(tutorEntity);

            var tutor = tutorRepository.findByUserName(tutorRegister.getUserName()).orElseThrow(() -> new IllegalArgumentException("Error in username and password"));
            var jwt = jwtService.genToken(tutor, null);

            PersonDetailsResponse response = new PersonDetailsResponse(jwt);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> educationExperience(EducationExperience educationExperience){
        try {
            Long userId = jwtService.getUserId();
            if(tutorRepository.existsById(userId)){
                return tutorRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(educationExperience.getHighestEducationLevelAttained()).ifPresent(existingUser::setHighestEducationLevelAttained);
                            Optional.ofNullable(educationExperience.getMajorFieldOfStudy()).ifPresent(existingUser::setMajorFieldOfStudy);
                            Optional.ofNullable(educationExperience.getTeachingGuide()).ifPresent(existingUser::setTeachingGuide);
                            Optional.ofNullable(educationExperience.getCurrentSchool()).ifPresent(existingUser::setCurrentSchool);
							Optional.ofNullable(educationExperience.getLocation()).ifPresent(existingUser::setLocation);
//                            existingUser = academicDetailMapper.mapFrom(academicDetail);

                            EducationExperience updatedUser = educationExperienceMapper.mapTo(tutorRepository.save(existingUser));

                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }
                        ).orElseThrow(() -> new UserNotFoundException("User not found"));

            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
       } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> teachingStyleApproach(TeachingStyleApproach teachingStyleApproach) {
        try {
            Long userId = jwtService.getUserId();
            if (tutorRepository.existsById(userId)) {
                return tutorRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(teachingStyleApproach.getTeachingStyle()).ifPresent(existingUser::setTeachingStyle);
                            Optional.ofNullable(teachingStyleApproach.getApproachToTutoring()).ifPresent(existingUser::setApproachToTutoring);
                            Optional.ofNullable(teachingStyleApproach.getAttendanceType()).ifPresent(existingUser::setAttendanceType);
//                            existingUser = academicDetailMapper.mapFrom(academicDetail);

                            TeachingStyleApproach updatedUser = teachingStyleApproachMapper.mapTo(tutorRepository.save(existingUser));

                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }
                ).orElseThrow(() -> new UserNotFoundException("User not found"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> availabilityPreference(AvailabilityPreference availabilityPreference){
        try {
            Long userId = jwtService.getUserId();
            if (tutorRepository.existsById(userId)) {
                return tutorRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(availabilityPreference.getPreferredSubjects()).ifPresent(existingUser::setPreferredSubjects);
                            Optional.ofNullable(availabilityPreference.getStudentAssessmentApproach()).ifPresent(existingUser::setStudentAssessmentApproach);
							Optional.ofNullable(availabilityPreference.getAvailableForAdditionalSupport()).ifPresent(existingUser::setAvailableForAdditionalSupport);
							Optional.ofNullable(availabilityPreference.getAvailableDate()).ifPresent(existingUser::setAvailableDate);
							Optional.ofNullable(availabilityPreference.getAvailableTime()).ifPresent(existingUser::setAvailableTime);
							
                            AvailabilityPreference updatedUser = availabilityPreferenceMapper.mapTo(tutorRepository.save(existingUser));

                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }
                ).orElseThrow(() -> new UserNotFoundException("User not found"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
	@Transactional
    public ResponseEntity<?> uploadDocuments(MultipartFile educationalCertificates, MultipartFile resumeCurriculumVitae, MultipartFile professionalDevelopmentCert, MultipartFile identificationDocuments){
         try {
        Long userId = jwtService.getUserId();
        if (tutorRepository.existsById(userId)) {
            return tutorRepository.findById(userId).map(
                existingUser -> {
                    try {
                        if (!validateFileSize(educationalCertificates) ||
                            !validateFileSize(resumeCurriculumVitae) ||
                            !validateFileSize(professionalDevelopmentCert) ||
                            !validateFileSize(identificationDocuments)){
                            return new ResponseEntity<>("One or more files exceed the maximum allowed size of 5MB", HttpStatus.BAD_REQUEST);
                        }

                        if (!educationalCertificates.isEmpty()) {
                            existingUser.setEducationalCertificates(educationalCertificates.getBytes());
                        }
                        if (!resumeCurriculumVitae.isEmpty()) {
                            existingUser.setResumeCurriculumVitae(resumeCurriculumVitae.getBytes());
                        }
                        if (!professionalDevelopmentCert.isEmpty()) {
                            existingUser.setProfessionalDevelopmentCert(professionalDevelopmentCert.getBytes());
                        }
                        if (!identificationDocuments.isEmpty()) {
                            existingUser.setIdentificationDocuments(identificationDocuments.getBytes());
                        }

                        tutorRepository.save(existingUser);
                        return new ResponseEntity<>(HttpStatus.CREATED);
                    } catch (IOException e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            ).orElseThrow(() -> new UserNotFoundException("User not found"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    } catch (Exception error) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    }

    private boolean validateFileSize(MultipartFile file) {
        return file.getSize() <= MAX_FILE_SIZE;
    }

public ResponseEntity<?> completeRegistration() {
    try {
        // Fetch the user entity by ID
        Long userId = jwtService.getUserId();
        TutorEntity tutorEntity = tutorRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Check if terms and conditions are approved
        if (!tutorEntity.isTermsAndConditionsApproved()) {
            // If terms and conditions are not approved, update the field to true
            tutorEntity.setTermsAndConditionsApproved(true);
            emailService.tutorRegistrationConfirmationEmail(tutorEntity.getEmail(), tutorEntity.getFirstName(), tutorEntity.getLastName(), tutorEntity.getEmail(), tutorEntity.getGender(), tutorEntity.getContactNumber(), tutorEntity.getAge(),tutorEntity.getHighestEducationLevelAttained(), tutorEntity.getMajorFieldOfStudy(), tutorEntity.getTeachingGuide() ,tutorEntity.getCurrentSchool(),tutorEntity.getTeachingStyle(),tutorEntity.getStudentAssessmentApproach(),tutorEntity.getAvailableForAdditionalSupport(),tutorEntity.getAvailableDate(),tutorEntity.getAvailableTime(),tutorEntity.getAttendanceType(),tutorEntity.getPreferredSubjects());
            tutorRepository.save(tutorEntity);

            // Optionally, you can update the user entity to mark registration as completed
            tutorEntity.setRegistrationCompleted(true);
            tutorRepository.save(tutorEntity);
            
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            // If terms and conditions are already approved, return a response indicating so
            return new ResponseEntity<>("Terms and conditions already approved", HttpStatus.BAD_REQUEST);
        }
    } catch (UserNotFoundException error) {
        return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception error) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    public LoginResponse login(LoginTutorRequest loginTutorRequest)
    {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginTutorRequest.getEmail(), loginTutorRequest.getPassword()));
        } catch (BadCredentialsException e){
            throw new IllegalArgumentException("Invalid email and Password", e);
        }
        // Try to find the user as a tutor
        var tutorOpt = tutorRepository.findByEmail(loginTutorRequest.getEmail());
        if (tutorOpt.isPresent()) {
            var tutor = tutorOpt.get();
            UserDetails userDetails = tutorService.userDetailsService().loadUserByUsername(tutor.getEmail());
            var jwt = jwtService.genToken(userDetails, tutor);
            TutorDto loggedInTutor = tutorMapper.mapTo(tutor);
            return new LoginResponse(null, loggedInTutor, jwt);
        }

        // If neither student nor tutor is found, throw an exception
        throw new IllegalArgumentException("Error in username and password");
    }

    @Override
    public String checkMail(String email) {
        return tutorRepository.findByEmail(email).map(
                existingUser -> {
                    String foundEmail = Optional.ofNullable(existingUser.getEmail()).orElse(null);
                    return foundEmail;
                }).orElseThrow(
                () -> new EmailNotFoundException("Email Not Found!!!")
        );
    }

}
