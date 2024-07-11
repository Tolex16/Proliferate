package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Admin.LoginAdminRequest;
import com.proliferate.Proliferate.Domain.DTO.Tutor.LoginTutorRequest;
import com.proliferate.Proliferate.Response.LoginResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface AdminManagementService {
    void createAdminUsers();

    LoginResponse login(LoginAdminRequest loginAdminRequest);

    void deleteStudent(String userName);

    void deleteTutor(String email);

    @Transactional
    Map<String, byte[]> getDocuments(String email, String documentType);
}
