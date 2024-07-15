package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Student.Submission;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.FeedbackDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.TutorManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
                    dto.setAssignedStudentName(assignment.getAssignedStudent().getFirstName());
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

    public Iterable<TutorEntity> getAllTutors() {
        return tutorRepository.findAll();
    }

    public Optional<TutorEntity> getTutorProfile(Long tutorId) {
        return tutorRepository.findById(tutorId);
    }

    public ClassSchedule createClassSchedule(Schedule schedule) {
        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setStudent(studentRepository.findById(schedule.getStudentId()).orElseThrow(() -> new UserNotFoundException("Student not found")));
        classSchedule.setTutor(tutorRepository.findById(schedule.getTutorId()).orElseThrow(() -> new UserNotFoundException("Tutor not found")));
        classSchedule.setSubject(subjectRepository.findById(schedule.getSubjectId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found")));
        classSchedule.setDate(schedule.getDate());
        classSchedule.setTime(schedule.getTime());
        classSchedule.setLocation(schedule.getLocation());
        return classScheduleRepository.save(classSchedule);
    }
    public List<ClassSchedule> getStudentSchedule() {
        Long studentId = jwtService.getUserId();
        return classScheduleRepository.findByStudentStudentId(studentId);
    }
    public Subject createSubject(SubjectDto subjectDto) {
        TutorEntity tutor = tutorRepository.findById(subjectDto.getTutorId()).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
        Subject subject = new Subject();
        subject.setTitle(subjectDto.getTitle());
        subject.setTutor(tutor);
        Long studentId = jwtService.getUserId();
        StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
        subject.setStudent(student);
        return subjectRepository.save(subject);
    }
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subject -> {
                    SubjectDto dto = new SubjectDto();
                    dto.setTitle(subject.getTitle());
                    dto.setTutorName(subject.getTutor().getFirstName() + " " + subject.getTutor().getLastName());

                    return dto;
                })
                .collect(Collectors.toList());
    }
    public SubjectDto getSubjectById(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new  UserNotFoundException("Student not found"));
                    SubjectDto dto = new SubjectDto();
                    dto.setTitle(subject.getTitle());
                    dto.setTutorName(subject.getTutor().getFirstName());

                    return dto;
    }

    public void deleteSubject(Long subjectId) {
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        if (subject.isPresent()) {
            subjectRepository.deleteById(subjectId);
        } else {
            throw new SubjectNotFoundException("Subject not found with id: " + subjectId);
        }
    }

	public List<Score> getStudentScores() {
        Long studentId = jwtService.getUserId();
        return scoreRepository.findByStudentStudentId(studentId);
    }
}
