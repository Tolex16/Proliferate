package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.SessionDto;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Student.Submission;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.FeedbackDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Response.SessionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface TutorManagementService {
    Feedback saveFeedback(FeedbackDto feedbackDto);
	
	Iterable<TutorEntity> getTutorsBySubjectTitle(Long subjectId);
	
    SessionResponse createSession(SessionDto sessionDto);
    void cancelSession(Long sessionId);
    Iterable<TutorEntity> getTutorsByStudentPayments();
    Optional<StudentEntity> getStudentDisplay();
    Optional<TutorEntity> getTutorProfile(Long tutorId);

    List<NotificationDTO> getNotificationsForStudent();
    ResponseEntity<?> uploadSolution(Submission submission);
	List<Score> getStudentScores();

    ClassSchedule createClassSchedule(Schedule schedule);

    ClassSchedule createRescheduling(Schedule schedule);
    List<ClassSchedule> getStudentSchedule();

    List<AssignmentDto> getStudentAssignments();

    List<SubjectDto> getAllSubjects();

    SubjectDto getSubjectById(Long subjectId);
	
	void deleteNotification(Long notificationId);

}
