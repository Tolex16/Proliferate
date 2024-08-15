package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.FriendInvite;
import com.proliferate.Proliferate.Domain.DTO.Student.SessionDto;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Student.Submission;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.FeedbackDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.EmailSendingException;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Response.SessionResponse;
import com.proliferate.Proliferate.Service.EmailService;
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
    private  final AdminRepository adminRepository;
	private final StudentRepository studentRepository;
	private final ScoreRepository scoreRepository;
    private final Mapper<StudentEntity, FriendInvite> friendInviteMapper;
    private final EmailService emailService;
    private final SessionRepository sessionRepository;
    private final NotificationRepository notificationRepository;
	private final AssignmentRepository assignmentRepository;

    private final SubjectRepository subjectRepository;
	private final ClassScheduleRepository classScheduleRepository;

    private final Mapper<Assignment, AssignmentDto> assignmentMapper;

    public Feedback saveFeedback(FeedbackDto feedbackDto) {
        Feedback feedback = new Feedback();

        Long studentId = jwtService.getUserId();
        StudentEntity student =studentRepository.findById(studentId).orElseThrow(() -> new  UserNotFoundException("Student not found"));
        feedback.setStudent(student);
        feedback.setTutor(tutorRepository.findById(feedbackDto.getTutorId()).orElseThrow(() -> new UserNotFoundException("Tutor not found")));
        feedback.setSubject(subjectRepository.findById(feedbackDto.getSubjectId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found")));
        feedback.setSessionDate(feedbackDto.getSessionDate());
        feedback.setRating(feedbackDto.getRating());
        feedback.setComments(feedbackDto.getComments());

        List<AdminEntity> admins = adminRepository.findAll();
        for (AdminEntity admin : admins) {
            Notifications notification = new Notifications();

            notification.setAdmin(admin);
            if (student.getStudentImage() != null) {
                notification.setProfileImage(student.getStudentImage());
            }
            notification.setType("Review and Rating Received");
            notification.setMessage("New review and rating: " + feedback.getStudent().getFirstName() + " " + feedback.getStudent().getLastName() + "has provided a review and rating for the session with " + feedback.getTutor().getFirstName() + " " + feedback.getTutor().getLastName() + ".");
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);
        }
        return feedbackRepository.save(feedback);
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
                StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Student not found"));

                emailService.sendAssignmentSubmissionEmail(student.getEmail(),student.getFirstName(),student.getLastName(),assignment.getTitle(),assignment.getSubject().getTitle());
                Notifications notification = new Notifications();

                notification.setTutor(assignment.getTutor());
                if (student.getStudentImage() != null) {
                    notification.setProfileImage(student.getStudentImage());
                }
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
        } catch (EmailSendingException e) {
            throw new RuntimeException(e);
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

	
	public Iterable<TutorEntity> getTutorsBySubjectTitleAndGrade(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
        String subjectTitle = subject.getTitle();
		
		Long studentId = jwtService.getUserId();
		StudentEntity student = studentRepository.findById(studentId)
                            .orElseThrow(() -> new UserNotFoundException("Student not found"));
        String gradeLevel = student.getGradeYear();
         return tutorRepository.findTutorsBySubjectAndGrade(subjectTitle, gradeLevel);
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
//                TutorEntity tutor = tutorRepository.findById(schedule.getTutorId()).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
//                classSchedule.setTutor(tutor);
                classSchedule.setDate(schedule.getDate());
                classSchedule.setTime(schedule.getTime());
                classSchedule.setReason(schedule.getReason());

                try {
                    emailService.sendClassRescheduledNotificationEmail(classSchedule.getTutor().getEmail(), student.getFirstName(),student.getLastName(), classSchedule.getTutor().getFirstName(),classSchedule.getTutor().getLastName(), schedule.getReason(), classScheduleOpt.get().getDate(), classScheduleOpt.get().getDate());
                } catch (EmailSendingException e) {
                    throw new RuntimeException(e);
                }
                Notifications notification = new Notifications();

                notification.setTutor(classSchedule.getTutor());
                if (student.getStudentImage() != null) {
                    notification.setProfileImage(student.getStudentImage());
                }
                notification.setType("Session Rescheduled by Student");
                notification.setMessage("Session rescheduled: " + student.getFirstName() + " " + student.getLastName() + "  has rescheduled the tutoring session with" + classSchedule.getTutor().getFirstName()
                        + " " + classSchedule.getTutor().getLastName() + " from "+ classScheduleOpt.get().getDate() + " and " + classScheduleOpt.get().getTime() + " to " + schedule.getDate() + " and " + schedule.getTime() + " due to " + schedule.getReason() + ".");
                notification.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notification);

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
        int frequency = sessionDto.getFrequency();
		session.setFrequency(frequency);
        session.setDuration(sessionDto.getDuration());
		
		// Calculate the price for the session

        double pricePerSession = calculatePrice(student,subject, sessionDto.getDuration());
        session.setDuration(sessionDto.getDuration());

        double price = pricePerSession * frequency;
        double HSTTax = price * 0.13;

        double pricePlusTaxes = price + HSTTax;

        double monthlyPrice = pricePlusTaxes * calculateMonthlyRate(frequency);

		 // Save the session
        Session savedSession = sessionRepository.save(session);
	
        Notifications notification = new Notifications();

        notification.setTutor(tutor);
        if (student.getStudentImage() != null) {
            notification.setProfileImage(student.getStudentImage());
        }
        notification.setType("Student Books a Tutoring Session");
        notification.setMessage("New session request: " + student.getFirstName() + " " + student.getLastName() + " has booked a tutoring session with you on " + student.getAvailability() + ". Please review and confirm.");
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

		    // Prepare the response with session ID and calculated price
        return new SessionResponse(savedSession.getSessionId(), monthlyPrice);
    }
	
private double calculatePrice(StudentEntity student, Subject subject, String duration) {
    String gradeLevel = determineGradeLevel(student, subject);
    double basePrice;

    // Handle pricing for KG to Grade 5
    if (gradeLevel.equals("KG") || gradeLevel.equals("1") || gradeLevel.equals("2") ||
        gradeLevel.equals("3") || gradeLevel.equals("4") || gradeLevel.equals("5")) {

        if (subject.getTitle().equalsIgnoreCase("Mathematics") || subject.getTitle().equalsIgnoreCase("English")) {
            if (duration.equalsIgnoreCase("1 hour")) {
                basePrice = 14;
            } else if (duration.equalsIgnoreCase("1.5 hours")) {
                basePrice = 21;
            } else {
                throw new IllegalArgumentException("Invalid duration for KG to Grade 5 Mathematics or English");
            }
        } else if (subject.getTitle().equalsIgnoreCase("Biology") || subject.getTitle().equalsIgnoreCase("Chemistry") || subject.getTitle().equalsIgnoreCase("Physics")) {
            if (duration.equalsIgnoreCase("1 hour")) {
                basePrice = 19;
            } else if (duration.equalsIgnoreCase("1.5 hours")) {
                basePrice = 28.5;
            } else {
                throw new IllegalArgumentException("Invalid duration for KG to Grade 5 Science subjects");
            }
        } else {
            throw new IllegalArgumentException("Invalid subject for KG to Grade 5");
        }

    // Handle pricing for Grades 6 to 12
    } else if (gradeLevel.equals("6") || gradeLevel.equals("7") || gradeLevel.equals("8") ||
               gradeLevel.equals("9") || gradeLevel.equals("10") || gradeLevel.equals("11") ||
               gradeLevel.equals("12")) {

        if (subject.getTitle().equalsIgnoreCase("Mathematics") || subject.getTitle().equalsIgnoreCase("English")) {
            if (duration.equalsIgnoreCase("1 hour")) {
                basePrice = 16;
            } else if (duration.equalsIgnoreCase("1.5 hours")) {
                basePrice = 24;
            } else {
                throw new IllegalArgumentException("Invalid duration for Grades 6 to 12 Mathematics or English");
            }
        } else if (subject.getTitle().equalsIgnoreCase("Biology")) {
            if (duration.equalsIgnoreCase("1 hour")) {
                basePrice = 20;
            } else if (duration.equalsIgnoreCase("1.5 hours")) {
                basePrice = 30;
            } else {
                throw new IllegalArgumentException("Invalid duration for Grades 6 to 12 Biology");
            }
        } else if (subject.getTitle().equalsIgnoreCase("Chemistry") || subject.getTitle().equalsIgnoreCase("Physics")) {
            if (duration.equalsIgnoreCase("1 hour")) {
                basePrice = 16;
            } else if (duration.equalsIgnoreCase("1.5 hours")) {
                basePrice = 24;
            } else {
                throw new IllegalArgumentException("Invalid duration for Grades 6 to 12 Chemistry or Physics");
            }
        } else {
            throw new IllegalArgumentException("Invalid subject for Grades 6 to 12");
        }

    // Handle pricing for "Any Grade" subjects
    } else if (gradeLevel.equalsIgnoreCase("Any Grade")) {
        if (subject.getTitle().equalsIgnoreCase("STEM (Coding & Robotics)")) {
            if (duration.equalsIgnoreCase("1 hour")) {
                basePrice = 20;
            } else if (duration.equalsIgnoreCase("1.5 hours")) {
                basePrice = 30;
            } else {
                throw new IllegalArgumentException("Invalid duration for Any Grade STEM (Coding & Robotics)");
            }
        } else if (subject.getTitle().equalsIgnoreCase("French") || subject.getTitle().equalsIgnoreCase("Spanish") || subject.getTitle().equalsIgnoreCase("German")) {
            if (duration.equalsIgnoreCase("1 hour")) {
                basePrice = 16;
            } else if (duration.equalsIgnoreCase("1.5 hours")) {
                basePrice = 24;
            } else {
                throw new IllegalArgumentException("Invalid duration for Any Grade language subjects");
            }
        } else {
            throw new IllegalArgumentException("Invalid subject for Any Grade");
        }
    } else {
        throw new IllegalArgumentException("Invalid grade level");
    }

    return basePrice;
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

    private int calculateMonthlyRate(int frequency) {
         int rate;

        if (frequency == 1 ){
            rate = 4;
        } else if (frequency == 2){
            rate = 8;
        } else if (frequency == 3){
            rate = 24;
        } else {
            throw new IllegalArgumentException("Invalid frequency");
        }

        return rate;
   }

    public void cancelSession(Long sessionId) {
        Optional<Session> session = sessionRepository.findById(sessionId);
        Long studentId = jwtService.getUserId();
        StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Student not found"));

        if (session.isPresent()) {
            sessionRepository.deleteById(sessionId);

            Notifications notification = new Notifications();

            notification.setStudent(student);
            if (student.getStudentImage() != null) {
                notification.setProfileImage(student.getStudentImage());
            }
            notification.setType("Session Request Cancellation by Student");
            notification.setMessage("Session canceled: You have canceled the tutoring session request with " + session.get().getTutor().getFirstName() + " " + session.get().getTutor().getLastName() +  " on " + student.getAvailability() + ".");
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
        dto.setProfileImage(Base64.getEncoder().encodeToString(notifications.getProfileImage()));
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

    public ResponseEntity<?> friendInvite(FriendInvite friendInvite){

        try {
            Long userId = jwtService.getUserId();;
            StudentEntity existingStudent = studentRepository.findById(userId).orElse(null);
            StudentEntity invitationEmail = friendInviteMapper.mapFrom(friendInvite);
            studentRepository.save(invitationEmail);
            emailService.sendInvitationEmail(friendInvite.getEmail(), friendInvite.getFriendName(), existingStudent.getFirstName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
