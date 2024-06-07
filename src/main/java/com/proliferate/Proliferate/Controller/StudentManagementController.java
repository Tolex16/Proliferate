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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    private final Mapper<StudentEntity, StudentProfile> studentProfileMapper;

    @GetMapping("/get-students")
    public ResponseEntity<Iterable<StudentTable>> getAllStudents() {
        List<StudentEntity> students = authenticationService.getAllStudents();
        Iterable<StudentTable> allStudent = studentMapper.mapListTo(students);
        return ResponseEntity.ok(allStudent);
    }

    @GetMapping("/get-studentProfile/{studentId}")
    public ResponseEntity<StudentProfile> getStudentProfile(@PathVariable Long studentId) {
        Optional<StudentEntity> studentEntityOptional = authenticationService.getStudentProfile(studentId);
        if (studentEntityOptional.isPresent()) {
            StudentProfile studentProfile = studentProfileMapper.mapTo(studentEntityOptional.get());
            return ResponseEntity.ok(studentProfile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


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