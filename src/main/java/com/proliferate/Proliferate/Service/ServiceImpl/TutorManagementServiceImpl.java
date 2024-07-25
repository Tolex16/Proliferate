package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.SessionDto;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Student.Submission;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.FeedbackDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Response.SessionResponse;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.TutorManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorManagementServiceImpl implements TutorManagementService {

    private final FeedbackRepository feedbackRepository;
    @Autowired
    private final JwtService jwtService;
    private final TutorRepository tutorRepository;

	private final StudentRepository studentRepository;
	private final ScoreRepository scoreRepository;

    private final SessionRepository sessionRepository;
    private final NotificationRepository notificationRepository;
	private final AssignmentRepository assignmentRepository;

    private final SubjectRepository subjectRepository;
	private final ClassScheduleRepository classScheduleRepository;

    private final Mapper<Assignment, AssignmentDto> assignmentMapper;

    public Feedback saveFeedback(FeedbackDto feedbackDto) {
        Feedback feedback = new Feedback();
        feedback.setTutor(tutorRepository.findById(feedbackDto.getTutorId()).orElseThrow(() -> new UserNotFoundException("Tutor not found")));
        feedback.setSubject(feedbackDto.getSubject());
        feedback.setSessionDate(feedbackDto.getSessionDate());
        feedback.setRating(feedbackDto.getRating());
        feedback.setComments(feedbackDto.getComments());

        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackByTutorId(Long tutorId) {
        return feedbackRepository.findByTutorTutorId(tutorId);
    }

    public double getAverageRating(Long tutorId) {
        List<Feedback> feedbacks = getFeedbackByTutorId(tutorId);
        return feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0);
    }

    public Optional<StudentEntity> getStudentDisplay() {
        Long studentId = jwtService.getUserId();
        return studentRepository.findById(studentId);

    }
	
	public ResponseEntity<?> uploadSolution(Submission submission) {
        try {
            Optional<Assignment> optionalAssignment = assignmentRepository.findById(submission.getAssignmentId());
            if (optionalAssignment.isPresent()) {
                Assignment assignment = optionalAssignment.get();
                if (!submission.getSolutionFile().isEmpty() && submission.getSolutionFile() != null) {
                    assignment.setAssignmentSolution(submission.getSolutionFile().getBytes());
                }

                assignmentRepository.save(assignment);

                Long studentId = jwtService.getUserId();
                Notifications notification = new Notifications();

                StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Student not found"));
                notification.setTutor(assignment.getTutor());

                notification.setType("Uploaded Answers by Student");
                notification.setMessage("Assignment Solution uploaded: " + assignment.getAssignedStudent().getFirstName() + " "+ assignment.getAssignedStudent().getLastName() + " " + "has uploaded the study's " +
                        "solution for " + assignment.getTutor().getFirstName() + " " + assignment.getTutor().getLastName() + ".");
                notification.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notification);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Assignment not found", HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("Error uploading solution", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	public List<AssignmentDto> getStudentAssignments() {
        Long studentId = jwtService.getUserId();
        StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new  UserNotFoundException("Student not found"));
        return assignmentRepository.findByAssignedStudent(student).stream()
                .map(assignment -> {
                    AssignmentDto dto = assignmentMapper.mapTo(assignment);
                    dto.setAssignedStudentName(assignment.getAssignedStudent().getFirstName() + " " + assignment.getAssignedStudent().getLastName());
                    dto.setDueDate(assignment.getDueDate());
                    dto.setTitle(assignment.getTitle());
                    dto.setSubjectName(assignment.getSubject().getTitle());
                    if (assignment.getAssignmentFile() != null) {
                        String base64File = Base64.getEncoder().encodeToString(assignment.getAssignmentFile());
                        dto.setAssignmentFileBase64(base64File);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

//    public Iterable<TutorEntity> getAllTutors() {
//        return tutorRepository.findAll();
//    }
	
	public Iterable<TutorEntity> getTutorsBySubjectTitle(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
        String subjectTitle = subject.getTitle();
        return tutorRepository.findTutorsByPreferredSubject(subjectTitle);
    }
	
	public Iterable<TutorEntity> getTutorsByStudentPayments() {
		Long studentId = jwtService.getUserId();
        return tutorRepository.findTutorsByStudentPayments(studentId);
    }

    public Optional<TutorEntity> getTutorProfile(Long tutorId) {
        return tutorRepository.findById(tutorId);
    }

    public ClassSchedule createClassSchedule(Schedule schedule) {
        ClassSchedule classSchedule = new ClassSchedule();
        Long studentId = jwtService.getUserId();
        StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Student not found"));
        classSchedule.setStudent(student);

        TutorEntity tutor = tutorRepository.findById(schedule.getTutorId()).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
        classSchedule.setTutor(tutor);
        Subject subject = subjectRepository.findById(schedule.getSubjectId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
        classSchedule.setSubject(subject);
        classSchedule.setDate(schedule.getDate());
        classSchedule.setTime(schedule.getTime());
        classSchedule.setSchedule(schedule.getSchedule());
        classSchedule.setLocation(schedule.getLocation());
        return classScheduleRepository.save(classSchedule);
    }

    public ClassSchedule createRescheduling(Schedule schedule) {
            Optional<ClassSchedule>  classScheduleOpt = classScheduleRepository.findById(schedule.getClassScheduleId());
            if (classScheduleOpt.isPresent()) {
                ClassSchedule classSchedule = classScheduleOpt.get();

                Long studentId = jwtService.getUserId();
                StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Student not found"));
                classSchedule.setStudent(student);

                classSchedule.setDate(schedule.getDate());
                classSchedule.setTime(schedule.getTime());
                classSchedule.setReason(schedule.getReason());
                return classScheduleRepository.save(classSchedule);
            }
            return new ResponseEntity<ClassSchedule>(HttpStatus.CREATED).getBody();
    }

    public List<ClassSchedule> getStudentSchedule() {
        Long studentId = jwtService.getUserId();
        return classScheduleRepository.findByStudentStudentId(studentId);
    }

    public SessionResponse createSession(SessionDto sessionDto) {

        TutorEntity tutor = tutorRepository.findById(sessionDto.getTutorId()).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
        Session session = new Session();
        session.setTutor(tutor);

        Long studentId = jwtService.getUserId();
        StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Student not found"));
        session.setStudent(student);

        Subject subject = subjectRepository.findById(sessionDto.getSubjectId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
        session.setSubject(subject);
		
		// Calculate the price for the session
        int frequency = sessionDto.getFrequency();
        double pricePerSession = calculatePrice(student,subject, frequency);
    
	
	    session.setFrequency(frequency);
        double price = pricePerSession * frequency;

		
		 // Save the session
        Session savedSession = sessionRepository.save(session);
	
        Notifications notification = new Notifications();

        notification.setTutor(tutor);

        notification.setType("Student Books a Tutoring Session");
        notification.setMessage("New session request: " + student.getFirstName() + " " + student.getLastName() + " has booked a tutoring session with you on " + student.getAvailability() + ". Please review and confirm.");
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

		    // Prepare the response with session ID and calculated price
        return new SessionResponse(savedSession.getSessionId(), price);
    }
	
  private double calculatePrice(StudentEntity student, Subject subject, int frequency) {

     String gradeLevel = determineGradeLevel(student, subject);
	
	// Example logic, you may need to refine this to match the exact structure of your pricing table
    if (gradeLevel.equals("K-5")) {
        if (subject.getTitle().equals("Mathematics")) {
            return frequency == 2 ? 140 : 190;
        } else if (subject.getTitle().equals("English")) {
            return frequency == 2 ? 140 : 190;
        }
    } else if (gradeLevel.equals("6-12")) {
        if (subject.getTitle().equals(("Mathematics"))) {
            return frequency == 2 ? 160 : 200;
        }else if (subject.getTitle().equals(("English"))) {
            return frequency == 2 ? 160 : 200;
        } else if (subject.getTitle().equals(("Biology"))) {
            return frequency == 2 ? 160 : 240;
        }else if (subject.getTitle().equals(("Chemistry"))) {
            return frequency == 2 ? 160 : 240;
        }else if (subject.getTitle().equals(("Physics"))) {
            return frequency == 2 ? 160 : 240;
        }
    } else if (gradeLevel.equals("Any Grade")) {
        if (subject.getTitle().equals(("STEM (Coding & Robotics)"))) {
            return frequency == 3 ? 200 : 300;
        } else if (subject.getTitle().equals(("French"))) {
            return frequency == 3 ? 160 : 190;
        } else if (subject.getTitle().equals(("Spanish"))) {
            return frequency == 3 ? 160 : 190;
        }else if (subject.getTitle().equals(("German"))) {
            return frequency == 3 ? 160 : 190;
        } if (subject.getTitle().equals(("Mathematics"))) {
            return frequency == 2 ? 160 : 200;
        } else if (subject.getTitle().equals(("English"))) {
            return frequency == 2 ? 160 : 200;
        }
    }

    throw new IllegalArgumentException("Invalid subject or grade level");
}
  private String determineGradeLevel(StudentEntity student, Subject subject) {
    // Example logic to determine if the subject falls under "Any Grade"
    List<String> anyGradeSubjects = Arrays.asList("STEM (Coding & Robotics)", "French", "Spanish", "German");

    // Check if the subject is one of the "Any Grade" subjects
    if (anyGradeSubjects.contains(subject.getTitle())) {
        return "Any Grade";
    }

    // Otherwise, use the specific grade level of the student
    return student.getGradeYear(); // Assuming this field exists in StudentEntity
  }


    public void cancelSession(Long sessionId) {
        Optional<Session> session = sessionRepository.findById(sessionId);
        Long studentId = jwtService.getUserId();
        StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Student not found"));

        if (session.isPresent()) {
            sessionRepository.deleteById(sessionId);

            Notifications notification = new Notifications();

            notification.setStudent(student);

            notification.setType("Session Request Cancellation by Student");
            notification.setMessage(  "Session canceled: You have canceled the tutoring session request with " + session.get().getTutor().getFirstName() + " " + session.get().getTutor().getLastName() +  " on " + student.getAvailability() + ".");
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        } else {
            throw new SubjectNotFoundException("Session not found with id: " + sessionId);
        }
    }

    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subject -> {
                    SubjectDto dto = new SubjectDto();
                    dto.setSubjectId(subject.getSubjectId());
                    dto.setTitle(subject.getTitle());

                    return dto;
                })
                .collect(Collectors.toList());
    }
    public SubjectDto getSubjectById(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new  UserNotFoundException("Student not found"));
                    SubjectDto dto = new SubjectDto();
                    dto.setTitle(subject.getTitle());

                    return dto;
    }

	public List<Score> getStudentScores() {
        Long studentId = jwtService.getUserId();
        return scoreRepository.findByStudentStudentId(studentId);
    }
    public List<NotificationDTO> getNotificationsForStudent() {
        Long studentId = jwtService.getUserId();
        List<Notifications> notifications = notificationRepository.findByStudentStudentId(studentId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private NotificationDTO convertToDto(Notifications notifications) {
        NotificationDTO dto = new NotificationDTO();
		dto.setNotificationId(notifications.getNotificationId());
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
	
	
	public void deleteNotification(Long notificationId) {
        Optional<Notifications> notification = notificationRepository.findById(notificationId);

        if (notification.isPresent()) {
            notificationRepository.deleteById(notificationId);
        } else {
            throw new SubjectNotFoundException("Notification not found with id: " + notificationId);
        }
    }
}
