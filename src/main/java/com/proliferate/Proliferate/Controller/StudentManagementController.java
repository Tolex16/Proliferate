package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentDto;
import com.proliferate.Proliferate.Domain.Entities.AttendanceEntity;
import com.proliferate.Proliferate.Repository.AttendanceRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Service.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StudentManagementController {
    @Autowired
    private final StudentManagementService authenticationService;

	private final AttendanceRepository attendanceRepository;
	
    private final TutorRepository tutorRepository;

    private final StudentRepository studentRepository;



//
//    public ResponseEntity<List<StudentDto>> getAllStudents() {
//        List<StudentDto> students = authenticationService.getAllStudents()
//                .stream()
//                .map(student -> new StudentDto(
//                        student.getUserId(),
//                        student.getFirstName() + " " + student.getLastName(),
//                        student.getSubjectsNeedingTutoring(),
//                        student.getGradeYear()
//                ))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(students);
//    }
@GetMapping("/get-students")
public ResponseEntity<Iterable<StudentDto>> getAllStudents(){
    ResponseEntity<Iterable<StudentDto>> studentDtos = authenticationService.getAllStudents();
    return authenticationService.getAllStudents();
}


	
//	@GetMapping("/{id}")
//    public ResponseEntity<StudentDto> getStudentProfile(@PathVariable Long id) {
//        StudentDto studentProfile = authenticationService.getStudentProfile(id);
//        return ResponseEntity.ok(studentProfile);
//    }
	

    @GetMapping("get-attendance-records")
    public ResponseEntity<List<AttendanceEntity>> getAllAttendanceRecords() {
        List<AttendanceEntity> attendanceRecords = authenticationService.getAllAttendanceRecords();
        return ResponseEntity.ok(attendanceRecords);
    }

    @PostMapping("add-attendance-records")
    public ResponseEntity<AttendanceEntity> addAttendanceRecord(@RequestBody AttendanceEntity attendanceEntity) {
        AttendanceEntity newAttendanceRecord = authenticationService.addAttendanceRecord(attendanceEntity);
        return ResponseEntity.ok(newAttendanceRecord);
    }
}