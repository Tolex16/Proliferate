package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentProfile;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentTable;
import com.proliferate.Proliferate.Domain.Entities.AttendanceEntity;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
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
    private final Mapper<StudentEntity, StudentTable> studentMapper;

    @GetMapping("/get-students")
    public ResponseEntity<Iterable<StudentTable>> getAllStudents() {
        List<StudentEntity> students = authenticationService.getAllStudents();
        Iterable<StudentTable> allStudent = studentMapper.mapListTo(students);
        return ResponseEntity.ok(allStudent);
    }

//    @GetMapping("/get-studentProfile")
//    public ResponseEntity<List<>> getAllStudentProfile() {
//        List<StudentEntity> students = authenticationService.getAllStudents();
//        List<StudentDto> studentDtos = studentMapper.mapListTo(students);
//        return ResponseEntity.ok(studentDtos);
//    }
//

	
//	@GetMapping("/{id}")
//    public ResponseEntity<StudentProfile> getStudentProfile(@PathVariable Long id) {
//        StudentProfile studentProfile = authenticationService.getStudentProfile(id);
//
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