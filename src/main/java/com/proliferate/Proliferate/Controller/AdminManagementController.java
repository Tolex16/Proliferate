package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Admin.StudentList;
import com.proliferate.Proliferate.Domain.DTO.Admin.TutorList;
import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentTable;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorTable;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.Subject;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Service.AdminManagementService;
import com.proliferate.Proliferate.Service.TutorAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminManagementController {
    @Autowired
    private final AdminManagementService managementService;
    private final Mapper<TutorEntity, TutorList> tutorListMapper;
    private final Mapper<StudentEntity, StudentList> studentMapper;

    @GetMapping("/documents")
    public ResponseEntity<Map<String, byte[]>> getDocuments(
            @RequestParam String email,
            @RequestParam String documentType) {
        try {
            Map<String, byte[]> documents = managementService.getDocuments(email, documentType);

            if (documents.values().stream().allMatch(Objects::isNull)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(documents, headers, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/notifications")
    public List<NotificationDTO> getNotificationsForAdmin() {
        return managementService.getNotificationsForAdmin();
    }
    @DeleteMapping("/student/delete/{userName}")
    public ResponseEntity<?> deleteStudent(@PathVariable String userName) {
        try {
            managementService.deleteStudent(userName);
            return ResponseEntity.ok("Student deleted successfully.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/tutor/delete/{email}")
    public ResponseEntity<?> deleteTutor(@PathVariable String email) {
        try {
            managementService.deleteTutor(email);
            return ResponseEntity.ok("Tutor deleted successfully.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/add-subject")
    public ResponseEntity<Subject> createSubject(@RequestBody SubjectDto subjectDto){
        Subject createdSubject = managementService.createSubject(subjectDto);
        return new ResponseEntity<>(createdSubject,HttpStatus.CREATED);
    }

    @DeleteMapping("/subject/delete/{subjectId}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long subjectId) {
        try {
            managementService.deleteSubject(subjectId);
            return ResponseEntity.ok("Subject deleted successfully.");
        } catch (SubjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
	
	@DeleteMapping("/delete-notification/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        try {
            managementService.deleteNotification(notificationId);
            return ResponseEntity.ok("Notification deleted successfully.");
        } catch (SubjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/get-alltutors")
    public ResponseEntity<Iterable<TutorList>> getAllTutors() {
        Iterable<TutorEntity> tutors = managementService.getAllTutors();
        Iterable<TutorList> allTutors = tutorListMapper.mapListTo(tutors);
        return ResponseEntity.ok(allTutors);
    }

    @GetMapping("/get-allstudents")
    public ResponseEntity<Iterable<StudentList>> getAllStudents() {
        Iterable<StudentEntity> students = managementService.getAllStudents();
        Iterable<StudentList> allStudent = studentMapper.mapListTo(students);
        return ResponseEntity.ok(allStudent);
    }
}
