package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Student.ReportDto;
import com.proliferate.Proliferate.Domain.DTO.Student.ScoreDto;
import com.proliferate.Proliferate.Domain.DTO.Student.TwoFactorAuthRequest;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.EducationExperience;
import com.proliferate.Proliferate.Domain.DTO.Tutor.FeedbackDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.GradeSubjects;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.*;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Service.EmailService;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentManagementServiceImpl implements StudentManagementService {

    private final AssignmentRepository assignmentRepository;

    private final AttendanceRepository attendanceRepository;

    private final Mapper<TutorEntity, GradeSubjects> gradeSubjectsMapper;
    private final ClassScheduleRepository classScheduleRepository;
    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;
    private final AdminRepository adminRepository;
    private final ScoreRepository scoreRepository;
    private final ReportRepository reportRepository;
    private final FeedbackRepository feedbackRepository;
    private final SubjectRepository subjectRepository;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private EmailService emailService;
    private final TutorRepository tutorRepository;
    private final NotificationRepository notificationRepository;
    private final Mapper<Assignment, AssignmentDto> assignmentMapper;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes

    public ResponseEntity<?> createAssignment(AssignmentDto assignmentDto) {
        try {
            if (!validateFileSize(assignmentDto.getAssignmentFile())) {
                throw new AssignmentNotCreatedException("Assignment attachment exceeds the maximum allowed size of 5MB");
            }
            Long tutorId = jwtService.getUserId();
            TutorEntity tutor = tutorRepository.findById(tutorId).orElseThrow(() -> new UserNotFoundException("Tutor not present"));
            Subject subject = subjectRepository.findById(assignmentDto.getSubjectId()).orElseThrow(() -> new SubjectNotFoundException("Subject not present"));
            StudentEntity student = studentRepository.findById(assignmentDto.getAssignedStudentId()).orElseThrow(() -> new UserNotFoundException("Student not present"));
            Assignment assignment = assignmentMapper.mapFrom(assignmentDto);
            if (!assignmentDto.getAssignmentFile().isEmpty() && assignmentDto.getAssignmentFile() != null) {
                assignment.setAssignmentFile(assignmentDto.getAssignmentFile().getBytes());
            }
            assignment.setTutor(tutor);
            assignment.setSubject(subject);
            assignment.setAssignedStudent(student);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dueDate = LocalDate.parse(assignmentDto.getDueDate(), formatter);
            assignment.setDueDate(dueDate);

            assignmentRepository.save(assignment);

            emailService.sendNewAssignmentNotificationEmail(student.getEmail(), student.getFirstName(), student.getLastName(), assignment.getTitle(), assignment.getSubject().getTitle(), assignment.getDueDate());
            List<AdminEntity> admins = adminRepository.findAll();
            for (AdminEntity admin : admins) {
                Notifications notification = new Notifications();
                notification.setAdmin(admin);
                if (tutor.getTutorImage() != null) {
                    notification.setProfileImage(tutor.getTutorImage());
                } else {
                    notification.setProfileImage(null); // Or set an empty string if preferred
                }
                notification.setType("Uploaded Assignment by Tutor");
                notification.setMessage("Assignment uploaded: " + tutor.getFirstName() + " " + tutor.getLastName() + " has uploaded an Assignment for " + student.getFirstName() + " " + student.getLastName() + ".");
                notification.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }

            Notifications notification1 = new Notifications();

            notification1.setStudent(student);
            if (tutor.getTutorImage() != null) {
                notification1.setProfileImage(tutor.getTutorImage());
            } else {
                notification1.setProfileImage(null); // Or set an empty string if preferred
            }

            notification1.setType("Uploaded Study Materials by Tutor");
            notification1.setMessage("Assignment available: " + tutor.getFirstName() + " " + tutor.getLastName() + " has uploaded Assignments. Please review them.");
            notification1.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification1);

            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (AssignmentNotCreatedException | IOException error) {
            throw new AssignmentNotCreatedException("Assignment could not be created");
        } catch (EmailSendingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AssignmentDto> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(assignment -> {
                    AssignmentDto dto = assignmentMapper.mapTo(assignment);
                    dto.setAssignedStudentName(assignment.getAssignedStudent().getFirstName() + " " + assignment.getAssignedStudent().getLastName());
                    if (assignment.getAssignmentFile() != null) {
                        String base64File = Base64.getEncoder().encodeToString(assignment.getAssignmentFile());

                        dto.setAssignmentFileBase64(base64File);
                    }
                    if (assignment.getAssignmentSolution() != null) {
                        String base64File = Base64.getEncoder().encodeToString(assignment.getAssignmentSolution());

                        dto.setAssignmentSolutionBase64(base64File);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteAssignment(Long assignmentId) {
        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
        if (assignment.isPresent()) {
            assignmentRepository.deleteById(assignmentId);
        } else {
            throw new AssignmentNotFoundException("Assignment not found with id: " + assignmentId);
        }
    }

    private boolean validateFileSize(MultipartFile file) {
        List<String> allowedContentTypes = Arrays.asList("application/pdf", "image/png", "image/jpeg", "application/msword");

        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            return false;
        }

        return file.getSize() <= MAX_FILE_SIZE;
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 1 * * *") // This cron expression runs every hour
    public void clearAssignmentsAfterDueDate() {
        List<Assignment> assignments = assignmentRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Assignment assignment : assignments) {
            try {
                LocalDate dueDate = assignment.getDueDate();
                LocalDateTime dueDateTime = dueDate.atStartOfDay(); // Convert LocalDate to LocalDateTime

                if (now.isAfter(dueDateTime.plusHours(2))) {
                    assignmentRepository.delete(assignment);
                }
            } catch (Exception e) {
                System.err.println("Error parsing due date for assignment ID " + assignment.getAssignmentId() + ": " + e.getMessage());
            }
        }
    }

    public List<FeedbackDto> getFeedbackByTutorId() {
        Long tutorId = jwtService.getUserId();
        List<Feedback> feedbacks = feedbackRepository.findByTutorTutorId(tutorId);
        return feedbacks.stream()
                .map(this::convertToFeedback)
                .collect(Collectors.toList());
    }

    private FeedbackDto convertToFeedback(Feedback feedback) {
        FeedbackDto dto = new FeedbackDto();
        dto.setStudentName(feedback.getStudent().getFirstName() + " " + feedback.getStudent().getLastName());
        dto.setSessionDate(feedback.getSessionDate());
        dto.setRating(feedback.getRating());
        dto.setComments(feedback.getComments());

        return dto;
    }

    public double getAverageRating() {
        List<FeedbackDto> feedbacks = getFeedbackByTutorId();
        return feedbacks.stream().mapToInt(FeedbackDto::getRating).average().orElse(0);
    }

    public void deleteFeedback(Long feedbackId) {
        Optional<Feedback> feedback = feedbackRepository.findById(feedbackId);
        if (feedback.isPresent()) {
            feedbackRepository.deleteById(feedbackId);
        } else {
            throw new SubjectNotFoundException("Feedback not found with id: " + feedbackId);
        }
    }

    public List<NotificationDTO> getNotificationsForTutor() {
        Long tutorId = jwtService.getUserId();
        List<Notifications> notifications = notificationRepository.findByTutorTutorId(tutorId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private NotificationDTO convertToDto(Notifications notifications) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notifications.getNotificationId());

        // Check if profileImage is null before encoding
        if (notifications.getProfileImage() != null) {
            dto.setProfileImage(Base64.getEncoder().encodeToString(notifications.getProfileImage()));
        } else {
            dto.setProfileImage(null); // Or set an empty string if preferred
        }

        dto.setType(notifications.getType());
        dto.setMessage(notifications.getMessage());
        dto.setTimeAgo(calculateTimeAgo(notifications.getCreatedAt()));

        return dto;
    }


    public String calculateTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 60) {
            return minutes + (minutes == 1 ? " min ago" : " mins ago");
        } else if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (days < 30) {
            return days + (days == 1 ? " day ago" : " days ago");
        } else {
            long months = days / 30;
            return months + (months == 1 ? " month ago" : " months ago");
        }
    }

    public void cancelSession(Long sessionId) {
        Optional<Session> session = sessionRepository.findById(sessionId);
        Long tutorId = jwtService.getUserId();
        TutorEntity tutor = tutorRepository.findById(tutorId).orElseThrow(() -> new UserNotFoundException("Tutor not present"));
        if (session.isPresent()) {

            sessionRepository.deleteById(sessionId);

            Notifications notification = new Notifications();

            notification.setStudent(session.get().getStudent());
            if (tutor.getTutorImage() != null) {
                notification.setProfileImage(tutor.getTutorImage());
            } else {
                notification.setProfileImage(null); // Or set an empty string if preferred
            }
            notification.setType("Session Request Declined by Tutor");
            notification.setMessage("Session declined: Unfortunately, " + tutor.getFirstName() + " " + tutor.getLastName() + " has declined your tutoring session request. Consider choosing another available tutor.");
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);

            List<AdminEntity> admins = adminRepository.findAll();
            for (AdminEntity admin : admins) {
                Notifications notification1 = new Notifications();

                notification1.setAdmin(admin);
                if (tutor.getTutorImage() != null) {
                    notification1.setProfileImage(tutor.getTutorImage());
                } else {
                    notification1.setProfileImage(null); // Or set an empty string if preferred
                }
                notification1.setType("Session Rescheduled by Tutor");
                notification1.setMessage("Session Rescheduled: " + tutor.getFirstName() + " " + tutor.getLastName() + "has canceled the tutoring session with " + session.get().getStudent().getFirstName() + " " + session.get().getStudent().getLastName() + " on " + session.get().getStudent().getAvailability() + ".");
                notification1.setCreatedAt(LocalDateTime.now());

                notificationRepository.save(notification1);
            }
        } else {
            throw new SubjectNotFoundException("Session not found with id: " + sessionId);
        }
    }


    public Optional<TutorEntity> getTutorDisplay() {
        Long tutorId = jwtService.getUserId();
        return tutorRepository.findById(tutorId);
    }

    public Iterable<StudentEntity> getStudentsByTutorPayments() {
        Long tutorId = jwtService.getUserId();
        return studentRepository.findStudentsByTutorPayments(tutorId);
    }


    public List<AttendanceEntity> getAllAttendanceRecords() {
        return attendanceRepository.findAll();
    }

    public AttendanceEntity addAttendanceRecord(AttendanceEntity attendanceEntity) {
        return attendanceRepository.save(attendanceEntity);
    }

    public List<ClassSchedule> getTutorSchedule() {
        Long tutorId = jwtService.getUserId();
        return classScheduleRepository.findByTutorTutorId(tutorId);
    }

    public Score addScore(ScoreDto scoreDto) {
        Score score = new Score();
        score.setStudent(studentRepository.findById(scoreDto.getStudentId()).orElseThrow(() -> new UserNotFoundException("Student not found")));
        score.setTest(scoreDto.getTest());
        score.setMarks(scoreDto.getMarks());
        score.setQuestionsAttempted(scoreDto.getQuestionsAttempted());
        score.setCorrectAnswers(scoreDto.getCorrectAnswers());
        score.setWrongAnswers(scoreDto.getWrongAnswers());
        score.setResult(scoreDto.getResult());

        return scoreRepository.save(score);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report addReport(ReportDto reportDto) {
        Report report = new Report();
        report.setStudent(studentRepository.findById(reportDto.getStudentId()).orElseThrow(() -> new UserNotFoundException("Student not found")));
        report.setSubject(subjectRepository.findById(reportDto.getSubjectId()).orElseThrow(() -> new SubjectNotFoundException("Subject not present")));
        report.setStatus(reportDto.getStatus());

        return reportRepository.save(report);
    }

    public void deleteNotification(Long notificationId) {
        Optional<Notifications> notification = notificationRepository.findById(notificationId);

        if (notification.isPresent()) {
            notificationRepository.deleteById(notificationId);
        } else {
            throw new SubjectNotFoundException("Notification not found with id: " + notificationId);
        }
    }

    public ResponseEntity<?> updateGradeSubjects(GradeSubjects gradeSubjects) {
        try {
            Long userId = jwtService.getUserId();
            if (tutorRepository.existsById(userId)) {
                return tutorRepository.findById(userId).map(
                        existingUser -> {
                            Optional.ofNullable(gradeSubjects.getTeachingGrade()).ifPresent(existingUser::setTeachingGrade);
                            Optional.ofNullable(gradeSubjects.getPreferredSubjects()).ifPresent(existingUser::setPreferredSubjects);

                            GradeSubjects gradeSubject = gradeSubjectsMapper.mapTo(tutorRepository.save(existingUser));

                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }
                ).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> enableTutor2fa(TwoFactorAuthRequest request) {
        try {
            Long tutorId = jwtService.getUserId();
            TutorEntity tutor = tutorRepository.findById(tutorId)
                    .orElseThrow(() -> new UserNotFoundException("Tutor not present"));

            // Check if the tutor already has 2FA enabled
            if (request.isEnable() && tutor.getIsTwoFactorAuthEnabled()) {
                return new ResponseEntity<>("2FA is already enabled for this tutor", HttpStatus.BAD_REQUEST);
            }

            // If enabling 2FA, update the tutor entity
            if (request.isEnable()) {
                tutor.setIsTwoFactorAuthEnabled(true);
                Optional.ofNullable(request.getTwoFactorAuthPhoneNumber()).ifPresent(tutor::setTwoFactorAuthPhoneNumber);
                //tutor.setTwoFactorAuthPhoneNumber(request.getTwoFactorAuthPhoneNumber());
            } else {
                // Disabling 2FA
                tutor.setIsTwoFactorAuthEnabled(false);
                tutor.setTwoFactorAuthPhoneNumber(null); // Clear the phone number if disabling 2FA
            }

            tutorRepository.save(tutor);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Map<String, Boolean> getTutor2faStatus() {
        Long tutorId = jwtService.getUserId();
        TutorEntity tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new UserNotFoundException("Tutor not present"));

        // Prepare the response map
        Map<String, Boolean> response = new HashMap<>();
        response.put("isTwoFactorAuthEnabled", tutor.getIsTwoFactorAuthEnabled());

        return response;

    }
}