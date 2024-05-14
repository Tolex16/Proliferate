package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.*;
import com.proliferate.Proliferate.Response.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity studentRegister(StudentRegisterPersDeets studentRegisterPersDeets);

    ResponseEntity tutorRegister(TutorRegister tutorRegister);
    ResponseEntity academicDetails(AcademicDetail academicDetail);

    ResponseEntity preference(Preferences preferences);

    ResponseEntity learningGoals(LearningGoals learningGoals);
	
	ResponseEntity completeRegistration();
	
    boolean isCurrentPasswordValid(String currentPassword);

    ResponseEntity changePassword(ChangePasswordRequest changePasswordRequest);

    Object findById(Long id);

    String getTermsAndConditions();

    String checkMail(String emailAddress);

    void updatePassword(String newPassword);
    LoginResponse login(LoginRequest loginRequest);
}
