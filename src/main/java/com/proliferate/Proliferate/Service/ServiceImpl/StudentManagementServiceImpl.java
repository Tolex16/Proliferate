package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Student.ScoreDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotCreatedException;
import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentManagementServiceImpl implements StudentManagementService {

    private final AssignmentRepository assignmentRepository;

    private final AttendanceRepository attendanceRepository;

    private final ClassScheduleRepository classScheduleRepository;
    private final StudentRepository studentRepository;

    private final AdminRepository adminRepository;
    private final ScoreRepository scoreRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    private final JwtService jwtService;

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
            Subject subject = subjectRepository.findByTitle(assignmentDto.getSubjectName()).orElseThrow(() -> new SubjectNotFoundException("Subject not present"));
            StudentEntity student = studentRepository.findById(assignmentDto.getAssignedStudentId()).orElseThrow(() -> new UserNotFoundException("Student not present"));
            Assignment assignment = assignmentMapper.mapFrom(assignmentDto);
            if (!assignmentDto.getAssignmentFile().isEmpty() && assignmentDto.getAssignmentFile() != null) {
                assignment.setAssignmentFile(assignmentDto.getAssignmentFile().getBytes());
            }
            assignment.setSubject(subject);
            assignment.setAssignedStudent(student);
			
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dueDate = LocalDate.parse(assignmentDto.getDueDate(), formatter);
            assignment.setDueDate(dueDate);

            assignmentRepository.save(assignment);

            List<AdminEntity> admins = adminRepository.findAll();
            for (AdminEntity admin : admins) {
                Notifications notification = new Notifications();
                notification.setAdmin(admin);
                notification.setType("Uploaded Assignment by Tutor");
                notification.setMessage("Assignment uploaded: " + tutor.getFirstName() + " " + tutor.getLastName() + " has uploaded an Assignment for " + student.getFirstName() + " " + student.getLastName() + ".");
                notification.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }

            Notifications notification1 = new Notifications();

            notification1.setStudent(student);
            notification1.setType("Uploaded Study Materials by Tutor");
            notification1.setMessage("Assignment available: " + tutor.getFirstName() + " " + tutor.getLastName() + " has uploaded Assignments. Please review them.");
            notification1.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification1);

            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (AssignmentNotCreatedException | IOException error) {
            throw new AssignmentNotCreatedException("Assignment could not be created");
        }
    }

    public List<AssignmentDto> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(assignment -> {
                    AssignmentDto dto = assignmentMapper.mapTo(assignment);
                    dto.setAssignedStudentName(assignment.getAssignedStudent().getFirstName());
                    if (assignment.getAssignmentFile() != null) {
                        String base64File = Base64.getEncoder().encodeToString(assignment.getAssignmentFile());
                        //String fileType = determineFileType(base64File);
                        dto.setAssignmentFileBase64(base64File);
                    }
                    if (assignment.getAssignmentSolution() != null) {
                        String base64File = Base64.getEncoder().encodeToString(assignment.getAssignmentSolution());
                        //String fileType = determineFileType(base64File);
                        dto.setAssignmentSolutionBase64(base64File);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

//    private String determineFileType(String base64File) {
//        // This is a simplified method to determine the file type based on its content
//        // You may want to use a more robust method in a real-world application
//        if (base64File.startsWith("JVBERi0")) {
//            return "application/pdf";
//        } else if (base64File.startsWith("/9j/")) {
//            return "image/jpeg";
//        } else if (base64File.startsWith("iVBORw0KGgo")) {
//            return "image/png";
//        } else {
//            return "application/octet-stream"; // Default fallback
//        }
//    }

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
	
    @Scheduled(cron = "0 0 * * * *") // This cron expression runs every hour
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
	
	
    public List<NotificationDTO> getNotificationsForTutor() {
        Long tutorId = jwtService.getUserId();
        List<Notifications> notifications = notificationRepository.findByTutorTutorId(tutorId);
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
	    public ResponseEntity<String> getSolutionFile(Long assignmentId) {
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(assignmentId);
        if (optionalAssignment.isPresent()) {
            Assignment assignment = optionalAssignment.get();
            byte[] solutionBytes = assignment.getAssignmentSolution();
            if (solutionBytes != null) {
                String base64Solution = Base64.getEncoder().encodeToString(solutionBytes);
                return new ResponseEntity<>(base64Solution, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Solution not found", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Assignment not found", HttpStatus.NOT_FOUND);
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

//    public Iterable<StudentEntity> getAllStudents() {
//        return studentRepository.findAll();
//    }

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
        score.setStudent(studentRepository.findById(scoreDto.getStudentId()).orElseThrow(() -> new UserNotFoundException("Tutor not found")));
        score.setTest(scoreDto.getTest());
        score.setMarks(scoreDto.getMarks());
        score.setQuestionsAttempted(scoreDto.getQuestionsAttempted());
        score.setCorrectAnswers(scoreDto.getCorrectAnswers());
        score.setWrongAnswers(scoreDto.getWrongAnswers());
        score.setResult(scoreDto.getResult());

        return scoreRepository.save(score);
    }

}