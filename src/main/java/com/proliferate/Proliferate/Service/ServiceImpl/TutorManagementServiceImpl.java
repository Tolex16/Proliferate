package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Service.TutorManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorManagementServiceImpl implements TutorManagementService {

    private final FeedbackRepository feedbackRepository;

    private final TutorRepository tutorRepository;

	private final StudentRepository studentRepository;
	private final ScoreRepository scoreRepository;
	
	private final AssignmentRepository assignmentRepository;

    private final SubjectRepository subjectRepository;
	private final ClassScheduleRepository classScheduleRepository;

    private final Mapper<Assignment, AssignmentDto> assignmentMapper;

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackByTutorName(String tutorName) {
        return feedbackRepository.findByTutorName(tutorName);
    }

    public double getAverageRating(String tutorName) {
        List<Feedback> feedbacks = getFeedbackByTutorName(tutorName);
        return feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0);
    }
	
	public List<AssignmentDto> getStudentAssignments(String studentName) {
        StudentEntity student = studentRepository.findByFirstName(studentName).orElseThrow(() -> new  UserNotFoundException("Student not found"));
        return assignmentRepository.findByAssignedStudent(student).stream()
                .map(assignment -> {
                    AssignmentDto dto = assignmentMapper.mapTo(assignment);
                    //dto.setAssignedStudentName(assignment.getAssignedStudent().getFirstName());
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
    public List<ClassSchedule> getStudentSchedule(Long studentId) {
        return classScheduleRepository.findByStudentStudentId(studentId);
    }
    public Subject createSubject(SubjectDto subjectDto) {
        TutorEntity tutor = tutorRepository.findByFirstName(subjectDto.getTutorName()).orElseThrow(() -> new UserNotFoundException("Tutor not found"));
        Subject subject = new Subject();
        subject.setTitle(subjectDto.getTitle());
        subject.setTutor(tutor);

        return subjectRepository.save(subject);
    }
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subject -> {
                    SubjectDto dto = new SubjectDto();
                    //dto.setAssignedStudentName(assignment.getAssignedStudent().getFirstName());
                    dto.setTitle(subject.getTitle());
                    dto.setTutorName(subject.getTutor().getFirstName());

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

	public List<Score> getStudentScores(Long studentId) {
        return scoreRepository.findByStudentStudentId(studentId);
    }
}
