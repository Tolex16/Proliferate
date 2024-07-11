package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotFoundException;
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

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminManagementController {
    @Autowired
    private final AdminManagementService managementService;

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

}
