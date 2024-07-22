package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Admin.LoginAdminRequest;
import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Service.AdminManagementService;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminManagementServiceImpl implements AdminManagementService {

   @Autowired
    private final PasswordEncoder passwordEncoder;

    private final TutorRepository tutorRepository;

    private final NotificationRepository notificationRepository;
    private final SubjectRepository subjectRepository;

    private final StudentRepository studentRepository;
    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    private final AdminRepository adminRepository;

    @PostConstruct
    public void createAdminUsers() {
        Optional<AdminEntity> adminUser = adminRepository.findByEmail("techproliferate@gmail.com");
        if (adminUser.isEmpty()) {
            AdminEntity createAdmin = new AdminEntity();
            createAdmin.setFirstName("tech");
            createAdmin.setLastName("proliferate");
            createAdmin.setEmail("techproliferate@gmail.com");
            createAdmin.setPassword(passwordEncoder.encode("Winner123!"));
            createAdmin.setRole(Role.ADMIN);
            adminRepository.save(createAdmin);
        }

        Optional<AdminEntity> adminUser2 = adminRepository.findByEmail("oseremio@gmail.com");
        if (adminUser2.isEmpty()) {
            AdminEntity createAdmin = new AdminEntity();
            createAdmin.setFirstName("tech");
            createAdmin.setLastName("oseremio");
            createAdmin.setEmail("oseremio@gmail.com");
            createAdmin.setPassword(passwordEncoder.encode("Winner123!"));
            createAdmin.setRole(Role.ADMIN);
            adminRepository.save(createAdmin);
        }
    }
	
    public LoginResponse login(LoginAdminRequest loginAdminRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginAdminRequest.getEmail(),
                            loginAdminRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Invalid admin email or password", e);
        }

        // Try to find the user as an admin
        var adminOpt = adminRepository.findByEmail(loginAdminRequest.getEmail());

        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(admin.getEmail());
            var jwt = jwtService.genToken(userDetails, admin);
            return new LoginResponse(null, null, jwt, null);
        }

        // If no admin is found, throw an exception
        throw new IllegalArgumentException("Invalid email or password");
    }

   @Transactional
    public void deleteStudent(String userName) {
        Optional<StudentEntity> student = studentRepository.findByUserName(userName);
        if (student.isPresent()) {
            studentRepository.deleteByUserName(userName);
        } else {
            throw new UserNotFoundException("Student not found with username: " + userName);
        }
    }

    @Transactional
    public void deleteTutor(String email) {
        Optional<TutorEntity> tutor = tutorRepository.findByEmail(email);
        if (tutor.isPresent()) {
            tutorRepository.deleteByEmail(email);
        } else {
            throw new UserNotFoundException("Tutor not found with email: " + email);
        }
    }

    @Transactional
    public Map<String, byte[]> getDocuments(String email, String documentType) {
        TutorEntity tutor = tutorRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Tutor not found"));

        Map<String, byte[]> documents = new HashMap<>();

        if ("all".equals(documentType)) {
            if (tutor.getEducationalCertificates() != null) {
                documents.put("educationalCertificates", tutor.getEducationalCertificates());
            }
            if (tutor.getResumeCurriculumVitae() != null) {
                documents.put("resumeCurriculumVitae", tutor.getResumeCurriculumVitae());
            }
            if (tutor.getProfessionalDevelopmentCert() != null) {
                documents.put("professionalDevelopmentCert", tutor.getProfessionalDevelopmentCert());
            }
            if (tutor.getIdentificationDocuments() != null) {
                documents.put("identificationDocuments", tutor.getIdentificationDocuments());
            }
        } else {
            switch (documentType) {
                case "educationalCertificates":
                    if (tutor.getEducationalCertificates() != null) {
                        documents.put("educationalCertificates", tutor.getEducationalCertificates());
                    }
                    break;
                case "resumeCurriculumVitae":
                    if (tutor.getResumeCurriculumVitae() != null) {
                        documents.put("resumeCurriculumVitae", tutor.getResumeCurriculumVitae());
                    }
                    break;
                case "professionalDevelopmentCert":
                    if (tutor.getProfessionalDevelopmentCert() != null) {
                        documents.put("professionalDevelopmentCert", tutor.getProfessionalDevelopmentCert());
                    }
                    break;
                case "identificationDocuments":
                    if (tutor.getIdentificationDocuments() != null) {
                        documents.put("identificationDocuments", tutor.getIdentificationDocuments());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid document type");
            }
        }

        return documents;
    }

    public List<NotificationDTO> getNotificationsForAdmin() {
        Long adminId = jwtService.getUserId();
        List<Notifications> notifications = notificationRepository.findByAdminAdminId(adminId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private NotificationDTO convertToDto(Notifications notifications) {
        NotificationDTO dto = new NotificationDTO();
        dto.setType(notifications.getType());
        dto.setMessage(notifications.getMessage());
        dto.setTimeAgo(calculateTimeAgo(notifications.getCreatedAt()));

        return dto;
    }
    private String calculateTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 60) {
            return minutes + " mins ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else {
            return days + " days ago";
        }
    }

    public Subject createSubject(SubjectDto subjectDto) {
        Subject subject = new Subject();
        subject.setSubjectId(subjectDto.getSubjectId() );
        subject.setTitle(subjectDto.getTitle());

        return subjectRepository.save(subject);
    }

    public void deleteSubject(Long subjectId) {
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        if (subject.isPresent()) {
            subjectRepository.deleteById(subjectId);
        } else {
            throw new SubjectNotFoundException("Subject not found with id: " + subjectId);
        }
    }
	
	public void deleteNotification(Long notificationId) {
        Optional<Notifications> notification = notificationRepository.findById(notificationId);

        if (notification.isPresent()) {
            notificationRepository.deleteById(notificationId);
        } else {
            throw new SubjectNotFoundException("Notification not found with id: " + notificationId);
        }
    }
}
