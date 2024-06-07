package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Student.FriendInvite;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.AttendanceEntity;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentManagementService {
    ResponseEntity createAssignment(AssignmentDto assignmentDto, MultipartFile assignmentFile);

	ResponseEntity<Iterable<StudentDto>> getAllStudents();
	
//	StudentDto getStudentProfile(Long id);
//
	List<AttendanceEntity> getAllAttendanceRecords();
	
	 AttendanceEntity addAttendanceRecord(AttendanceEntity attendanceEntity);
}
