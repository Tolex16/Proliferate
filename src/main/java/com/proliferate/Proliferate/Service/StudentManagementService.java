package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.AttendanceEntity;
import com.proliferate.Proliferate.Domain.Entities.ClassSchedule;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
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

	ClassSchedule saveClassSchedule(ClassSchedule classSchedule);

	List<ClassSchedule> getAllClassSchedule();
}
