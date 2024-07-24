package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Tutor.*;
import com.proliferate.Proliferate.Domain.Entities.AdminEntity;
import com.proliferate.Proliferate.Domain.Entities.Notifications;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.AccountNotVerifiedException;
import com.proliferate.Proliferate.ExeceptionHandler.StudentEmailPresentException;
import com.proliferate.Proliferate.ExeceptionHandler.UserAlreadyExistsException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.AdminRepository;
import com.proliferate.Proliferate.Repository.NotificationRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Response.PersonDetailsResponse;
import com.proliferate.Proliferate.Service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TutorAuthenticationServiceImpl implements TutorAuthenticationService {
    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final TutorRepository tutorRepository;

    private final StudentRepository studentRepository;

    private final AdminRepository adminRepository;

    private final NotificationRepository notificationRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    private final UserService tutorService;
    private final Mapper<TutorEntity, TutorRegister> tutorRegisterMapper;

    private final Mapper<TutorEntity, EducationExperience> educationExperienceMapper;

    private final Mapper<TutorEntity, TeachingStyleApproach> teachingStyleApproachMapper;

    private final Mapper<TutorEntity, AvailabilityPreference> availabilityPreferenceMapper;

    private final Mapper<TutorEntity, UpdateTutor> updateTutorMapper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
    private final EmailService emailService;

    private final Mapper<TutorEntity, TutorDto> tutorMapper;
    @Autowired
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> tutorRegister(TutorRegister tutorRegister){
        if(tutorRepository.existsByEmail(tutorRegister.getEmail())){
            throw new UserAlreadyExistsException("There is a tutor account associated with this email already");
        }
        if(studentRepository.existsByEmail(tutorRegister.getEmail())){
            throw new StudentEmailPresentException("There is an student account associated with this email already");
        }
        try {
            tutorRegister.setPassword(passwordEncoder.encode(tutorRegister.getPassword()));
            TutorEntity tutorEntity = tutorRegisterMapper.mapFrom(tutorRegister);
            tutorEntity.setRole(Role.TUTOR);
            tutorRepository.save(tutorEntity);

            var tutor = tutorRepository.findByEmail(tutorRegister.getEmail()).orElseThrow(() -> new IllegalArgumentException("Error in username and password"));
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
                            Optional.ofNullable(educationExperience.getTeachingGrade()).ifPresent(existingUser::setTeachingGrade);
                            Optional.ofNullable(educationExperience.getYearsOfTeachingExperience()).ifPresent(existingUser::setYearsOfTeachingExperience);
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
                            Optional.ofNullable(availabilityPreference.getWeeklyAvailability()).ifPresent(existingUser::setWeeklyAvailability);
							Optional.ofNullable(availabilityPreference.getTimeslotAvailability()).ifPresent(existingUser::setTimeslotAvailability);
                            Optional.ofNullable(availabilityPreference.getSelectTimezone()).ifPresent(existingUser::setSelectTimezone);
                            Optional.ofNullable(availabilityPreference.getCommunicationLanguage()).ifPresent(existingUser::setCommunicationLanguage);

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
                            return new ResponseEntity<>("One or more files exceed the maximum allowed size of 5MB or Invalid fi", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
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
            ).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    } catch (Exception error) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    }
	
	
	private boolean validateFileSize(MultipartFile file) {
    List<String> allowedContentTypes = Arrays.asList("application/pdf", "image/png", "image/jpeg");

    String contentType = file.getContentType();
    if (contentType == null || !allowedContentTypes.contains(contentType)) {
        return false;
    }
    
    return file.getSize() <= MAX_FILE_SIZE;
}

@Transactional
public ResponseEntity<?> completeRegistration() {
    try {
        Long userId = jwtService.getUserId();
        TutorEntity tutorEntity = tutorRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Tutor not found"));
        tutorEntity.setVerificationToken(generateToken());
        tutorEntity.setTokenExpirationTime(LocalDateTime.now().plusMinutes(5));
        tutorEntity.setEmailVerified(false);

        String confirmLink = "https://proliferate-site.vercel.app/auth/account-verified?token=" + tutorEntity.getVerificationToken();

            tutorEntity  = getTutorWithLob(userId);
            emailService.tutorRegistrationConfirmationEmail(
                    tutorEntity.getEmail(),
                    tutorEntity.getFirstName(),
                    tutorEntity.getLastName(),
                    tutorEntity.getEmail(),
                    tutorEntity.getGender(),
                    tutorEntity.getContactNumber(),
                    tutorEntity.getAge(),
                    tutorEntity.getHighestEducationLevelAttained(),
                    tutorEntity.getMajorFieldOfStudy(),
                    tutorEntity.getYearsOfTeachingExperience(),
                    tutorEntity.getTeachingGrade(),
                    tutorEntity.getCurrentSchool(),
                    tutorEntity.getTeachingStyle(),
                    tutorEntity.getWeeklyAvailability(),
                    tutorEntity.getTimeslotAvailability(),
                    tutorEntity.getAttendanceType(),
                    tutorEntity.getPreferredSubjects(),
                    confirmLink);
            tutorEntity.setTermsAndConditionsApproved(true);
			// Persist changes to termsAndConditionsApproved
           // entityManager.merge(tutorEntity);
			
			// Persist changes to registrationCompleted
            tutorEntity.setRegistrationCompleted(true);
            entityManager.merge(tutorEntity);

        List<AdminEntity> admins = adminRepository.findAll();
        for (AdminEntity admin : admins) {
            Notifications notification = new Notifications();
            notification.setAdmin(admin);
            notification.setType("Tutor Applies to Join");
            notification.setMessage("New tutor application: A tutor has applied to join the platform. " +
                    "Please review their profile and qualifications.");
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return new ResponseEntity<>(tutorEntity.getVerificationToken(),HttpStatus.OK);

    } catch (UserNotFoundException error) {
        return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception error) {
        // Log full stack trace for debugging
        error.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


    @Transactional
    public LoginResponse login(LoginTutorRequest loginTutorRequest)
    {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginTutorRequest.getEmail(),
                            loginTutorRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e){
            throw new IllegalArgumentException("Invalid tutor email and Password", e);
        }
        // Try to find the user as a tutor

        var tutorOpt = tutorRepository.findByEmailAndEmailVerifiedIsTrue(loginTutorRequest.getEmail());

        if (tutorOpt.isPresent()) {
            var tutor = tutorOpt.get();
                UserDetails userDetails = tutorService.userDetailsService().loadUserByUsername(tutor.getEmail());
                var jwt = jwtService.genToken(userDetails, tutor);
                TutorDto loggedInTutor = tutorMapper.mapTo(tutor);
				boolean hasBioPresent = hasBio(tutor); // Check if tutor has bio
                boolean hasImagePresent = hasImage(tutor); // Check if tutor has image

            if (!hasImagePresent) {
                Notifications notification = new Notifications();

                notification.setTutor(tutor);
//                if (tutor.getTutorImage() != null) {
//                    notification.setProfileImage(Base64.getEncoder().encodeToString(tutor.getTutorImage()));
//                } else {
//                    notification.setProfileImage(null); // or set a default image, if applicable
//                }
                notification.setType("Request for Profile Update");
                notification.setMessage("Profile update required: Please update your profile with your photo, ID and relevant certificate to make your profile visible on our platform.");
                notification.setCreatedAt(LocalDateTime.now());

                notificationRepository.save(notification);
            }
                return new LoginResponse(null, loggedInTutor, jwt, hasBioPresent);
        } else throw new AccountNotVerifiedException("Account not verified for this tutor, please check your email to verify");
    }
	
    public boolean hasBio(TutorEntity tutor) {
        return tutor.getBio() != null && !tutor.getBio().isEmpty();
    }

    public boolean hasImage(TutorEntity tutor) {
        return tutor.getTutorImage() != null;
    }

    public String generateToken(){
        List rules = Arrays.asList(new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1));

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generatePassword(6,rules);
        return password;
    }

    @Transactional
	@Override
    public Map<String, Boolean> checkMail(String email) {
        Map<String, Boolean> result = new HashMap<>();

        boolean isEmailPresent = tutorRepository.findByEmail(email).isPresent();
        result.put("email", isEmailPresent);

        // Check if the email is present in the student repository
        boolean isEmailStudentPresent = studentRepository.findByEmail(email).isPresent();
        result.put("email", isEmailStudentPresent);

        return result;
	}

	
    public ResponseEntity<?> updateTutor(UpdateTutor updateTutor) {
        try {
            Long userId = jwtService.getUserId();
            if (tutorRepository.existsById(userId)) {
                return tutorRepository.findById(userId).map(
                        existingUser -> {
                            if (updateTutor.getTutorImage() != null && !updateTutor.getTutorImage().isEmpty()) {
                                if (!validateFileSize(updateTutor.getTutorImage())) {
                                    return new ResponseEntity<>("Tutor Image exceeds the maximum allowed size of 5MB", HttpStatus.BAD_REQUEST);
                                }
								if (!updateTutor.getTutorImage().isEmpty()) {
                                try {
                                    existingUser.setTutorImage(updateTutor.getTutorImage().getBytes());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
								  }
                            }
                            Optional.ofNullable(updateTutor.getFirstName()).ifPresent(existingUser::setFirstName);
                            Optional.ofNullable(updateTutor.getLastName()).ifPresent(existingUser::setLastName);
                            Optional.ofNullable(updateTutor.getEmail()).ifPresent(existingUser::setEmail);
                            Optional.ofNullable(updateTutor.getPhoneNumber()).ifPresent(existingUser::setContactNumber);
                            Optional.ofNullable(updateTutor.getBio()).ifPresent(existingUser::setBio);


                            UpdateTutor updatedTutor = updateTutorMapper.mapTo(tutorRepository.save(existingUser));

                            return new ResponseEntity<>(updatedTutor,HttpStatus.CREATED);
                        }
                ).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public TutorEntity getTutorWithoutLob(Long userId) {
        return entityManager.createQuery(
            "SELECT t FROM TutorEntity t WHERE t.tutorId = :userId", TutorEntity.class)
            .setParameter("userId", userId)
            .getSingleResult();
    }

    @Transactional
    public TutorEntity getTutorWithLob(Long userId) {
        TutorEntity tutor = getTutorWithoutLob(userId);
        // Access the LOB fields to trigger their lazy loading
        tutor.getEducationalCertificates();
        tutor.getResumeCurriculumVitae();
        tutor.getProfessionalDevelopmentCert();
        tutor.getIdentificationDocuments();
        tutor.getTutorImage();
        return tutor;
    }
	
}
