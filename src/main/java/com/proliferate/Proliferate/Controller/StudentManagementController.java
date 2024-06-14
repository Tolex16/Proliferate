package com.proliferate.Proliferate.Controller;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentProfile;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentTable;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.AttendanceEntity;
import com.proliferate.Proliferate.Domain.Entities.ClassSchedule;
import com.proliferate.Proliferate.Domain.Entities.Feedback;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotCreatedException;
import com.proliferate.Proliferate.Service.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tutor")
@RequiredArgsConstructor
public class StudentManagementController {
    @Autowired
    private final StudentManagementService authenticationService;
    private final Mapper<StudentEntity, StudentTable> studentMapper;

    private final Mapper<StudentEntity, StudentProfile> studentProfileMapper;

    @PostMapping(path = "/create-assignment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addAssignment(@ModelAttribute AssignmentDto assignmentDto, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        try {
            authenticationService.createAssignment(assignmentDto);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (AssignmentNotCreatedException ex) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
        }
    }
	
	    @GetMapping("/all-assignments")
    public ResponseEntity<List<AssignmentDto>> getAllAssignments() {
        List<AssignmentDto> assignments = authenticationService.getAllAssignments();
        if (CollectionUtils.isEmpty(assignments)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }


    @GetMapping("/get-students")
    public ResponseEntity<Iterable<StudentTable>> getAllStudents() {
        Iterable<StudentEntity> students = authenticationService.getAllStudents();
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

    @GetMapping("/get-attendance-records")
    public ResponseEntity<List<AttendanceEntity>> getAllAttendanceRecords() {
        List<AttendanceEntity> attendanceRecords = authenticationService.getAllAttendanceRecords();
        return ResponseEntity.ok(attendanceRecords);
    }

    @PostMapping("/add-attendance-records")
    public ResponseEntity<AttendanceEntity> addAttendanceRecord(@RequestBody AttendanceEntity attendanceEntity) {
        AttendanceEntity newAttendanceRecord = authenticationService.addAttendanceRecord(attendanceEntity);
        return ResponseEntity.ok(newAttendanceRecord);
    }

    @PostMapping("/add-class-schedule")
    public ResponseEntity<ClassSchedule> addClassSchedule(@RequestBody ClassSchedule classSchedule) {
        ClassSchedule newClassSchedule = authenticationService.saveClassSchedule(classSchedule);
        return ResponseEntity.ok(newClassSchedule);
    }

    @GetMapping("/get-class-schedule")
    public ResponseEntity<List<ClassSchedule>> getAllClassSchedule() {
        List<ClassSchedule> classSchedules = authenticationService.getAllClassSchedule();
        return ResponseEntity.ok(classSchedules);
    }

}