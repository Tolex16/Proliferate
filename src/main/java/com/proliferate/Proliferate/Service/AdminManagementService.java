package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Admin.LoginAdminRequest;
import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.LoginTutorRequest;
import com.proliferate.Proliferate.Domain.Entities.Subject;
import com.proliferate.Proliferate.Response.LoginResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface AdminManagementService {
    void createAdminUsers();

    LoginResponse login(LoginAdminRequest loginAdminRequest);

    void deleteStudent(String userName);

    void deleteTutor(String email);

    @Transactional
    Map<String, byte[]> getDocuments(String email, String documentType);

    List<NotificationDTO> getNotificationsForAdmin();
    Subject createSubject(SubjectDto subjectDto);
    void deleteSubject(Long subjectId);
}
