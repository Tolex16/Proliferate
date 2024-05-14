package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.*;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.UserEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.EmailNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserAlreadyExistsException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.UserRepository;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Response.PersonDetailsResponse;
import com.proliferate.Proliferate.Service.AuthenticationService;
import com.proliferate.Proliferate.Service.EmailService;
import com.proliferate.Proliferate.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final Mapper<UserEntity, StudentRegisterPersDeets> studentRegisterPersDeetsMapper;

    private final Mapper<UserEntity, TutorRegister> tutorRegisterMapper;

    private final Mapper<UserEntity, AcademicDetail> academicDetailMapper;

    private final Mapper<UserEntity, Preferences> preferencesMapper;

    private final Mapper<UserEntity, LearningGoals> learningGoalsMapper;
    private final EmailService emailService;

    private final Mapper<UserEntity, UserDto> userMapper;
    @Autowired
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> studentRegister(StudentRegisterPersDeets studentRegisterPersDeets){
        if(userRepository.existsByUserName(studentRegisterPersDeets.getUserName())){
            throw new UserAlreadyExistsException("There is an account with this username.");
        }
        if(userRepository.existsByEmail(studentRegisterPersDeets.getEmail())){
            throw new UserAlreadyExistsException("There is an account associated with this email already");
        }
        try {
            studentRegisterPersDeets.setPassword(passwordEncoder.encode(studentRegisterPersDeets.getPassword()));
            UserEntity userEntity = studentRegisterPersDeetsMapper.mapFrom(studentRegisterPersDeets);
            userEntity.setRole(Role.STUDENT);
            userRepository.save(userEntity);

            var user = userRepository.findByUserName(studentRegisterPersDeets.getUserName()).orElseThrow(() -> new IllegalArgumentException("Error in username and password"));
            var jwt = jwtService.genToken(user);

            PersonDetailsResponse response = new PersonDetailsResponse(jwt);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> tutorRegister(TutorRegister tutorRegister){
        if(userRepository.existsByUserName(tutorRegister.getUserName())){
            throw new UserAlreadyExistsException("There is an account with this username.");
        }
        if(userRepository.existsByEmail(tutorRegister.getEmail())){
            throw new UserAlreadyExistsException("There is an account associated with this email already");
        }
        try {
            tutorRegister.setPassword(passwordEncoder.encode(tutorRegister.getPassword()));
            UserEntity userEntity = tutorRegisterMapper.mapFrom(tutorRegister);
            userEntity.setRole(Role.TUTOR);
            userRepository.save(userEntity);
            //emailService.sendRegistrationConfirmationEmail(tutorRegister.getEmail(), tutorRegister.getFirstName(), tutorRegister.getEmail(),tutorRegister.getGender(),tutorRegister.getContactNumber(), tutorRegister.getAge(),tutorRegister.getGradeYear());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> academicDetails(AcademicDetail academicDetail){
        try {
            Long userId = jwtService.getUserId();
            if(userRepository.existsById(userId)){
                return userRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(academicDetail.getGradeYear()).ifPresent(existingUser::setGradeYear);
                            Optional.ofNullable(academicDetail.getSubjectsNeedingTutoring()).ifPresent(existingUser::setSubjectsNeedingTutoring);
                            Optional.ofNullable(academicDetail.getAttendanceType()).ifPresent(existingUser::setAttendanceType);
                            Optional.ofNullable(academicDetail.getCurrentLocation()).ifPresent(existingUser::setCurrentLocation);
//                            existingUser = academicDetailMapper.mapFrom(academicDetail);

                            AcademicDetail updatedUser = academicDetailMapper.mapTo(userRepository.save(existingUser));

                            return new ResponseEntity<>( updatedUser,HttpStatus.CREATED);
                        }
                        ).orElseThrow(() -> new UserNotFoundException("User not found"));

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
            if (userRepository.existsById(userId)) {
                return userRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(preferences.getAvailability()).ifPresent(existingUser::setAvailability);
                            Optional.ofNullable(preferences.getAdditionalPreferences()).ifPresent(existingUser::setAdditionalPreferences);
                            Optional.ofNullable(preferences.getRequirements()).ifPresent(existingUser::setRequirements);
                            Optional.ofNullable(preferences.getCommunicationLanguage()).ifPresent(existingUser::setCommunicationLanguage);
//                            existingUser = academicDetailMapper.mapFrom(academicDetail);

                            Preferences updatedUser = preferencesMapper.mapTo(userRepository.save(existingUser));

                            return new ResponseEntity<>(updatedUser, HttpStatus.CREATED);
                        }
                ).orElseThrow(() -> new UserNotFoundException("User not found"));
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
            if (userRepository.existsById(userId)) {
                return userRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(learningGoals.getShortTermGoals()).ifPresent(existingUser::setShortTermGoals);
                            Optional.ofNullable(learningGoals.getLongTermGoals()).ifPresent(existingUser::setLongTermGoals);

                            LearningGoals updatedUser = learningGoalsMapper.mapTo(userRepository.save(existingUser));

                            return new ResponseEntity<>(updatedUser, HttpStatus.CREATED);
                        }
                ).orElseThrow(() -> new UserNotFoundException("User not found"));
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
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Check if terms and conditions are approved
        if (!userEntity.isTermsAndConditionsApproved()) {
            // If terms and conditions are not approved, update the field to true
            userEntity.setTermsAndConditionsApproved(true);
            emailService.sendRegistrationConfirmationEmail(userEntity.getEmail(), userEntity.getFirstName(), userEntity.getEmail(), userEntity.getGender(), userEntity.getContactNumber(), userEntity.getAge(),userEntity.getGradeYear(), userEntity.getSubjectsNeedingTutoring());
            userRepository.save(userEntity);

            // Optionally, you can update the user entity to mark registration as completed
            userEntity.setRegistrationCompleted(true);
            userRepository.save(userEntity);
            
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

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest) {
        try {
            Long userId = jwtService.getUserId();
            return userRepository.findById(userId).map(
                    existingUser -> {

                        existingUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                        existingUser.setPassword(passwordEncoder.encode(changePasswordRequest.getConfirmNewPassword()));
                        userRepository.save(existingUser);
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
            ).orElseThrow(() -> new RuntimeException("User Not Found"));
        } catch (RuntimeException e) {
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


    @Override
    public boolean isCurrentPasswordValid(String currentPassword) {
        UserEntity currentUser = getCurrentUser();
        return currentUser != null && passwordEncoder.matches(currentPassword, currentUser.getPassword());
    }

    @Override
    public void updatePassword(String newPassword) {
        UserEntity currentUser = getCurrentUser();
        if (currentUser != null){
            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(currentUser);
        } else {
            ResponseEntity.badRequest().body("User not found");
        }
    }

    private static UserEntity getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity){
            return (UserEntity) authentication.getPrincipal();
        }
        return null;
    }
    public LoginResponse login(LoginRequest loginRequest)
    {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));
        } catch (BadCredentialsException e){
            throw new IllegalArgumentException("Invalid username and Password", e);
        }

        var user = userRepository.findByUserName(loginRequest.getUserName()).orElseThrow(() -> new IllegalArgumentException("Error in username and password"));
        var jwt = jwtService.genToken(user);

        UserDto loggedInUser = userMapper.mapTo(user);
        return new LoginResponse(loggedInUser, jwt);
    }

    public UserEntity findById(Long id) {

        return userRepository.findById(id).get();
    }

    @Override
    public String checkMail(String email) {
        return userRepository.findByEmail(email).map(
                existingUser -> {
                    String foundEmail = Optional.ofNullable(existingUser.getEmail()).orElse(null);
                    return foundEmail;
                }).orElseThrow(
                () -> new EmailNotFoundException("Email Not Found!!!")
        );
    }

}
