package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.ScoreDto;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Student.TestDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface StudentManagementService {
    ResponseEntity createAssignment(AssignmentDto assignmentDto);

    List<AssignmentDto> getAllAssignments();
	
	Iterable<StudentEntity> getAllStudents();

	Optional<StudentEntity> getStudentProfile(Long studentId);

	List<AttendanceEntity> getAllAttendanceRecords();
	
	AttendanceEntity addAttendanceRecord(AttendanceEntity attendanceEntity);

	ClassSchedule createClassSchedule(Schedule schedule);

	List<ClassSchedule> getTutorSchedule(Long tutorId);

	void deleteAssignment(Long assignmentId);

	Score addScore(ScoreDto scoreDto);

	Test addTest(TestDto testDto);
}
