package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Student.ScoreDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface StudentManagementService {
    ResponseEntity createAssignment(AssignmentDto assignmentDto);

    List<AssignmentDto> getAllAssignments();
	
	//Iterable<StudentEntity> getAllStudents();

	Iterable<StudentEntity> getStudentsByTutorPayments();

	Optional<TutorEntity> getTutorDisplay();

	List<AttendanceEntity> getAllAttendanceRecords();
	
	AttendanceEntity addAttendanceRecord(AttendanceEntity attendanceEntity);

	List<ClassSchedule> getTutorSchedule();

	void deleteAssignment(Long assignmentId);
	List<NotificationDTO> getNotificationsForTutor();

	Score addScore(ScoreDto scoreDto);

	void clearAssignmentsAfterDueDate();

}
