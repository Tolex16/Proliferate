package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.*;
import com.proliferate.Proliferate.Domain.DTO.Student.*;
import com.proliferate.Proliferate.Response.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface StudentAuthenticationService {
	
    ResponseEntity studentRegister(StudentRegisterPersDeets studentRegisterPersDeets);

	
    ResponseEntity academicDetails(AcademicDetail academicDetail);

    ResponseEntity preference(Preferences preferences);

    ResponseEntity learningGoals(LearningGoals learningGoals);
	
	ResponseEntity completeRegistration();

    String getTermsAndConditions();

    String checkUsername(UsernameVerification usernameVerification);

    LoginResponse login(LoginStudentRequest loginStudentRequest);
}
