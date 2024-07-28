package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.*;
import com.proliferate.Proliferate.Domain.DTO.Student.AcademicDetail;
import com.proliferate.Proliferate.Domain.DTO.Student.LearningGoals;
import com.proliferate.Proliferate.Domain.DTO.Student.Preferences;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentRegisterPersDeets;
import com.proliferate.Proliferate.Domain.Entities.AdminEntity;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Response.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface ChangePasswordService {
	
    ResponseEntity<?> changeStudentPassword(StudentEntity student, ChangePasswordRequest request);
    ResponseEntity<?> changeTutorPassword(TutorEntity tutor, ChangePasswordRequest request);

    ResponseEntity<?> changeAdminPassword(AdminEntity admin, ChangePasswordRequest request);
}
