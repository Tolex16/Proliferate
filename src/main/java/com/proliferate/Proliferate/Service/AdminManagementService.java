package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Admin.LoginAdminRequest;
import com.proliferate.Proliferate.Domain.DTO.Tutor.LoginTutorRequest;
import com.proliferate.Proliferate.Response.LoginResponse;

public interface AdminManagementService {
    void createAdminUsers();

    LoginResponse login(LoginAdminRequest loginAdminRequest);
}
