package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.FeedbackDto;
import com.proliferate.Proliferate.Domain.Entities.*;

import java.util.List;
import java.util.Optional;

public interface TutorManagementService {
    Feedback saveFeedback(FeedbackDto feedbackDto);

    List<Feedback> getFeedbackByTutorId(Long tutorId);

    double getAverageRating(Long tutorId);

    Iterable<TutorEntity> getAllTutors();
    Optional<StudentEntity> getStudentDisplay();
    Optional<TutorEntity> getTutorProfile(Long tutorId);
	
	List<Score> getStudentScores();

    ClassSchedule createClassSchedule(Schedule schedule);

    List<ClassSchedule> getStudentSchedule();

    List<AssignmentDto> getStudentAssignments();

    Subject createSubject(SubjectDto subjectDto);
    List<SubjectDto> getAllSubjects();

    SubjectDto getSubjectById(Long subjectId);

    void deleteSubject(Long subjectId);
}
