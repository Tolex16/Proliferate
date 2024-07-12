package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Student.*;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.*;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Response.PersonDetailsResponse;
import com.proliferate.Proliferate.Service.*;
import lombok.RequiredArgsConstructor;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentAuthenticationServiceImpl implements StudentAuthenticationService {
    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final StudentRepository studentRepository;

    private final Mapper<StudentEntity, UpdateStudent> updateStudentMapper;

    private final TutorRepository tutorRepository;
    @Autowired
    private final UserService userService;
	
	@Autowired
	private final TokenService tokenService;
	
    private final Mapper<StudentEntity, StudentRegisterPersDeets> studentRegisterPersDeetsMapper;

    private final Mapper<StudentEntity, AcademicDetail> academicDetailMapper;

    private final Mapper<StudentEntity, Preferences> preferencesMapper;

    private final Mapper<StudentEntity, LearningGoals> learningGoalsMapper;
    
	private final EmailService emailService;

    private final Mapper<StudentEntity, StudentDto> studentMapper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
	
    @Autowired
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> studentRegister(StudentRegisterPersDeets studentRegisterPersDeets){
        if(studentRepository.existsByUserName(studentRegisterPersDeets.getUserName())){
            throw new UserAlreadyExistsException("There is an student account with this username.");
        }
        if(studentRepository.existsByEmail(studentRegisterPersDeets.getEmail())){
            throw new UserAlreadyExistsException("There is an student account associated with this email already");
        }
        if(tutorRepository.existsByEmail(studentRegisterPersDeets.getEmail())){
            throw new TutorEmailPresentException("There is a tutor account associated with this email already");
        }
        try {
            studentRegisterPersDeets.setPassword(passwordEncoder.encode(studentRegisterPersDeets.getPassword()));
            StudentEntity studentEntity = studentRegisterPersDeetsMapper.mapFrom(studentRegisterPersDeets);
            studentEntity.setRole(Role.STUDENT);
            studentRepository.save(studentEntity);

            var student = studentRepository.findByUserName(studentRegisterPersDeets.getUserName()).orElseThrow(() -> new IllegalArgumentException("Error in username and password"));
            var jwt = jwtService.genToken(student, null);

            PersonDetailsResponse response = new PersonDetailsResponse(jwt);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> academicDetails(AcademicDetail academicDetail){
        try {
            Long userId = jwtService.getUserId();
            if(studentRepository.existsById(userId)){
                return studentRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(academicDetail.getGradeYear()).ifPresent(existingUser::setGradeYear);
                            Optional.ofNullable(academicDetail.getSubjectsNeedingTutoring()).ifPresent(existingUser::setSubjectsNeedingTutoring);
                            Optional.ofNullable(academicDetail.getAttendanceType()).ifPresent(existingUser::setAttendanceType);
							Optional.ofNullable(academicDetail.getPreferredTime()).ifPresent(existingUser::setPreferredTime);
                            Optional.ofNullable(academicDetail.getCurrentLocation()).ifPresent(existingUser::setCurrentLocation);


                            AcademicDetail updatedStudent = academicDetailMapper.mapTo(studentRepository.save(existingUser));

                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }
                        ).orElseThrow(() -> new UserNotFoundException("Student account not found"));

            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
       } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> preference(Preferences preferences) {
        try {
            Long userId = jwtService.getUserId();
            if (studentRepository.existsById(userId)) {
                return studentRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(preferences.getAvailability()).ifPresent(existingUser::setAvailability);
                            Optional.ofNullable(preferences.getAdditionalPreferences()).ifPresent(existingUser::setAdditionalPreferences);
                            Optional.ofNullable(preferences.getRequirements()).ifPresent(existingUser::setRequirements);
                            Optional.ofNullable(preferences.getCommunicationLanguage()).ifPresent(existingUser::setCommunicationLanguage);
//                            existingUser = academicDetailMapper.mapFrom(academicDetail);

                            Preferences updatedStudent = preferencesMapper.mapTo(studentRepository.save(existingUser));

                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }
                ).orElseThrow(() -> new UserNotFoundException("Student account not found"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> learningGoals(LearningGoals learningGoals){
        try {
            Long userId = jwtService.getUserId();
            if (studentRepository.existsById(userId)) {
                return studentRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(learningGoals.getShortTermGoals()).ifPresent(existingUser::setShortTermGoals);
                            Optional.ofNullable(learningGoals.getLongTermGoals()).ifPresent(existingUser::setLongTermGoals);

                            LearningGoals updatedStudent = learningGoalsMapper.mapTo(studentRepository.save(existingUser));

                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }
                ).orElseThrow(() -> new UserNotFoundException("Student account not found"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

public ResponseEntity<?> completeRegistration() {
    try {
        // Fetch the user entity by ID
        Long userId = jwtService.getUserId();
        StudentEntity studentEntity = studentRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Student not found"));
        studentEntity.setVerificationToken(generateToken());
        studentEntity.setTokenExpirationTime(LocalDateTime.now().plusMinutes(10));
        studentEntity.setEmailVerified(false);

        String confirmLink = "https://proliferate-site.vercel.app/auth/account-verified?token=" + studentEntity.getVerificationToken();

            emailService.studentRegistrationConfirmationEmail(
                    studentEntity.getEmail(),
                    studentEntity.getFirstName(),
                    studentEntity.getLastName(),
                    studentEntity.getEmail(),
                    studentEntity.getGender(),
                    studentEntity.getContactNumber(),
                    studentEntity.getAge(),
                    studentEntity.getGradeYear(),
                    studentEntity.getSubjectsNeedingTutoring(),
                    studentEntity.getAttendanceType(),
                    studentEntity.getAvailability(),
                    studentEntity.getAdditionalPreferences(),
                    studentEntity.getShortTermGoals(),
                    studentEntity.getLongTermGoals(),
                    confirmLink);
            
            // If terms and conditions are not approved, update the field to true
            studentEntity.setTermsAndConditionsApproved(true);
            // Optionally, you can update the user entity to mark registration as completed
            studentEntity.setRegistrationCompleted(true);
            studentRepository.save(studentEntity);
            
            return new ResponseEntity<>(HttpStatus.OK);

    } catch (UserNotFoundException error) {
        return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception error) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@Override
public ResponseEntity<?> verifyToken(String token) {
    try {
        // Try to find the student by token
        Optional<StudentEntity> studentOpt = studentRepository.findByVerificationToken(token);
        
        // Try to find the tutor by token
        Optional<TutorEntity> tutorOpt = tutorRepository.findByVerificationToken(token);

        if (studentOpt.isEmpty() && tutorOpt.isEmpty()) {
            return new ResponseEntity<>("Invalid token", HttpStatus.NOT_FOUND);
        }

        // Verify student token if student is present
        if (studentOpt.isPresent()) {
            StudentEntity studentEntity = studentOpt.get();
            if (studentEntity.getTokenExpirationTime().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>("Token expired", HttpStatus.BAD_REQUEST);
            }
            studentEntity.setEmailVerified(true);
            studentEntity.setVerificationToken(null);
            studentRepository.save(studentEntity);
        }

        // Verify tutor token if tutor is present
        if (tutorOpt.isPresent()) {
            TutorEntity tutorEntity = tutorOpt.get();
            if (tutorEntity.getTokenExpirationTime().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>("Token expired", HttpStatus.BAD_REQUEST);
            }
            tutorEntity.setEmailVerified(true);
            tutorEntity.setVerificationToken(null);
            tutorRepository.save(tutorEntity);
        }

        return new ResponseEntity<>("Email verified successfully", HttpStatus.OK);

    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

	
	 public String getTermsAndConditions() {
        StringBuilder termsAndConditions = new StringBuilder();

        termsAndConditions.append("Welcome to Proliferate Learning Management System (LMS). Before you proceed with using our platform, please carefully read and understand the following terms and conditions:");
        termsAndConditions.append("\n\nAcceptance of Terms: By accessing or using the Proliferate LMS platform, you agree to be bound by these terms and conditions, our privacy policy, and all applicable laws and regulations. If you do not agree with any part of these terms, you may not access or use the platform.");
        termsAndConditions.append("\n\nUser Registration: You must register an account with accurate and complete information to access certain features of the platform. You are responsible for maintaining the confidentiality of your account credentials and for all activities that occur under your account.");
        termsAndConditions.append("\n\nUse of Platform: Proliferate LMS provides a platform for educational purposes, including access to learning materials, tutoring services, and communication tools. You agree to use the platform only for lawful purposes and in accordance with these terms and conditions.");
        termsAndConditions.append("\n\nUser Content: You retain ownership of any content you submit, upload, or post on the platform. By providing content, you grant Proliferate LMS a worldwide, non-exclusive, royalty-free license to use, modify, reproduce, and distribute your content for the purpose of operating and improving the platform.");
        termsAndConditions.append("\n\nTutoring Services: Proliferate LMS connects students with tutors for educational purposes. We do not guarantee the availability, quality, or effectiveness of tutoring services, and we are not responsible for any interactions or disputes between users.");
        termsAndConditions.append("\n\nPayment and Billing: Certain features or services on the platform may require payment. By using these features or services, you agree to pay any applicable fees and charges. All payments are non-refundable unless otherwise stated. If a student is unable to attend a class, they may have the option to reschedule the class subject to the tutor's availability. However, if no early notification of class rescheduling is provided, the missed class will not be refunded. In the event of a missed class due to the tutor's unavailability or technical issues, the tutor will offer an alternative class schedule.");
        termsAndConditions.append("\n\nPlatform Content and Intellectual Property: All content available on the platform, including courses, materials, and resources, are the intellectual property of Proliferate and its tutors. Any unauthorized use, reproduction, or distribution of the content is strictly prohibited. Online tutors retain the rights to their course content. Learners may not share, reproduce, or distribute course materials without the tutor's permission.");
        termsAndConditions.append("\n\nCode of Conduct: Users must conduct themselves respectfully and professionally while interacting on the platform. Any form of harassment, discrimination, or inappropriate behavior will not be tolerated and may result in account termination.");
        termsAndConditions.append("\n\nPrivacy and Security: Your privacy and security are important to us. Please review our privacy policy to understand how we collect, use, and protect your personal information.");
        termsAndConditions.append("\n\nModifications to Terms: Proliferate LMS reserves the right to update or modify these terms and conditions at any time without prior notice. Your continued use of the platform after any such changes constitutes your acceptance of the revised terms.");
        termsAndConditions.append("\n\nTermination of Account: Proliferate LMS reserves the right to suspend or terminate your account and access to the platform at any time for any reason, including but not limited to violation of these terms and conditions.");
        termsAndConditions.append("\n\nContact Us: If you have any questions or concerns about these terms and conditions, please contact us at contact@proliferate.ai");
        termsAndConditions.append("\n\nBy clicking \"I agree\" or by accessing and using the Proliferate LMS platform, you acknowledge that you have read, understood, and agreed to these terms and conditions.");

        return termsAndConditions.toString();
    }

@Transactional
  public LoginResponse login(LoginStudentRequest loginStudentRequest) {
      try {
          // Authenticate the user
          authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(
                          loginStudentRequest.getUserName(),
                          loginStudentRequest.getPassword()
                  )
          );
      } catch (BadCredentialsException e) {
          throw new IllegalArgumentException("Invalid student username or password", e);
      }

      // Try to find the user as a student first
      var studentOpt = studentRepository.findByUserNameAndEmailVerifiedIsTrue(loginStudentRequest.getUserName());
      if (studentOpt.isPresent()) {
          var student = studentOpt.get();
          UserDetails userDetails = userService.userDetailsService().loadUserByUsername(student.getUsername());
          var jwt = jwtService.genToken(userDetails, student);
          StudentDto loggedInStudent = studentMapper.mapTo(student);
          return new LoginResponse(loggedInStudent, null, jwt);

      } else throw new AccountNotVerifiedException("Account not verified for this student, please check your email to verify");
  }
    public String generateToken(){
        List<CharacterRule> rules = Arrays.asList(new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1));

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generatePassword(6,rules);
        return password;
    }

     public void logout(String token) {
        Instant expiryTime = jwtService.extractExpiration(token).toInstant();
        tokenService.addToken(token, expiryTime);
    }


	
    @Override
    public Map<String, Boolean> checkStudent(String username, String email) {
    Map<String, Boolean> result = new HashMap<>();

    // Check if the userName is present in the student repository
    boolean isUserNamePresent = studentRepository.findByUserName(username).isPresent();
    result.put("userName", isUserNamePresent);

    // Check if the email is present in the student repository
    boolean isEmailPresent = studentRepository.findByEmail(email).isPresent();
    result.put("email", isEmailPresent);

    // Check if the email is present in the tutor repository
    boolean isEmailTutorPresent = tutorRepository.findByEmail(email).isPresent();
    result.put("email", isEmailTutorPresent);

        return result;
}


    public ResponseEntity<?> updateStudent(UpdateStudent updateStudent) {
        try {
            Long userId = jwtService.getUserId();
            if (studentRepository.existsById(userId)) {
                return studentRepository.findById(userId).map(
                        existingUser -> {
                            if (updateStudent.getStudentImage() != null && !updateStudent.getStudentImage().isEmpty()) {
                                if (!validateFileSize(updateStudent.getStudentImage())) {
                                    return new ResponseEntity<>("Student Image exceeds the maximum allowed size of 5MB", HttpStatus.BAD_REQUEST);
                                }
                                if (!updateStudent.getStudentImage().isEmpty()) {
                                    try {
                                        existingUser.setStudentImage(updateStudent.getStudentImage().getBytes());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                            Optional.ofNullable(updateStudent.getFirstName()).ifPresent(existingUser::setFirstName);
                            Optional.ofNullable(updateStudent.getLastName()).ifPresent(existingUser::setLastName);
                            Optional.ofNullable(updateStudent.getEmail()).ifPresent(existingUser::setEmail);
                            Optional.ofNullable(updateStudent.getPhoneNumber()).ifPresent(existingUser::setContactNumber);
                            Optional.ofNullable(updateStudent.getBio()).ifPresent(existingUser::setBio);


                            UpdateStudent updatedStudent = updateStudentMapper.mapTo(studentRepository.save(existingUser));

                            return new ResponseEntity<>(updatedStudent,HttpStatus.CREATED);
                        }
                ).orElseThrow(() -> new UserNotFoundException("Student not found"));
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
}
