package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.*;

import java.util.List;
import java.util.Optional;

public interface TutorManagementService {
    Feedback saveFeedback(Feedback feedback);

    List<Feedback> getFeedbackByTutorName(String tutorName);

    double getAverageRating(String tutorName);

    Optional<TutorEntity> getTutorProfile(Long tutorId);
	
	List<Score> getStudentScores(Long studentId);

    ClassSchedule createClassSchedule(Schedule schedule);

    List<ClassSchedule> getStudentSchedule(Long studentId);

    List<AssignmentDto> getStudentAssignments(String studentName);

    Subject createSubject(SubjectDto subjectDto);
    List<SubjectDto> getAllSubjects();

    SubjectDto getSubjectById(Long subjectId);

    void deleteSubject(Long subjectId);
}
