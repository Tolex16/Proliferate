package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Student.*;
import com.proliferate.Proliferate.Response.LoginResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface StudentAuthenticationService {
	
    ResponseEntity studentRegister(StudentRegisterPersDeets studentRegisterPersDeets);

	
    ResponseEntity academicDetails(AcademicDetail academicDetail);

    ResponseEntity preference(Preferences preferences);

    ResponseEntity learningGoals(LearningGoals learningGoals);
	
	ResponseEntity completeRegistration();
	
	ResponseEntity<?> verifyToken(String token);
    
    String getTermsAndConditions();

    void logout(String token);

    Map<String, Boolean> checkStudent(String username, String email);

    LoginResponse login(LoginStudentRequest loginStudentRequest);

    ResponseEntity<?> updateStudent(UpdateStudent updateStudent);
}
