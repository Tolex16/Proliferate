package com.proliferate.Proliferate.Controller;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.proliferate.Proliferate.Domain.DTO.NotificationDTO;
import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.*;
import com.proliferate.Proliferate.Domain.DTO.Tutor.*;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotCreatedException;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.Service.TutorManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class TutorManagementController {
    @Autowired
    private final TutorManagementService feedbackService;

    private final Mapper<TutorEntity, TutorProfile> tutorProfileMapper;
    private final Mapper<StudentEntity, StudentDisplay> studentDisplayMapper;
    private final Mapper<TutorEntity, TutorTable> tutorTableMapper;

    @PostMapping("/create-feedback")
    public Feedback saveFeedback(@RequestBody FeedbackDto feedback) {
        return feedbackService.saveFeedback(feedback);
    }

    @GetMapping("/feedback/{tutorId}")
    public List<Feedback> getFeedbackByTutorName(@PathVariable Long tutorId) {
        return feedbackService.getFeedbackByTutorId(tutorId);
    }

    @GetMapping("/feedback/{tutorId}/average")
    public double getAverageRating(@PathVariable Long tutorId) {
        return feedbackService.getAverageRating(tutorId);
    }

    @GetMapping("/get-tutors")
    public ResponseEntity<Iterable<TutorTable>> getAllStudents() {
        Iterable<TutorEntity> tutors = feedbackService.getTutorsByStudentPayments();
        Iterable<TutorTable> allTutors = tutorTableMapper.mapListTo(tutors);
        return ResponseEntity.ok(allTutors);
    }

    @GetMapping("/get-bio")
    public ResponseEntity<StudentDisplay> getStudentDisplay() {
        Optional<StudentEntity> studentEntityOptional = feedbackService.getStudentDisplay();
        if (studentEntityOptional.isPresent()) {
            StudentDisplay studentDisplay = studentDisplayMapper.mapTo(studentEntityOptional.get());
            return ResponseEntity.ok(studentDisplay);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/get-tutorProfile/{tutorId}")
    public ResponseEntity<TutorProfile> getTutorProfile(@PathVariable Long tutorId) {
        Optional<TutorEntity> tutorEntityOptional = feedbackService.getTutorProfile(tutorId);
        if (tutorEntityOptional.isPresent()) {
            TutorProfile tutorProfile = tutorProfileMapper.mapTo(tutorEntityOptional.get());
            return ResponseEntity.ok(tutorProfile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping(path = "/add-solution", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addSolution(@ModelAttribute Submission submission, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        try {
            feedbackService.uploadSolution(submission);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (AssignmentNotCreatedException ex) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
        }
    }

    @GetMapping("/notifications")
    public List<NotificationDTO> getNotificationsForStudent() {
        return feedbackService.getNotificationsForStudent();
    }

    @GetMapping("/scores")
    public ResponseEntity<List<Score>> getStudentScores() {
        List<Score> scores = feedbackService.getStudentScores();
        return ResponseEntity.ok(scores);
    }

    @PostMapping("/create-class-schedule")
    public ClassSchedule createClassSchedule(@RequestBody Schedule schedule) {
        return feedbackService.createClassSchedule(schedule);
    }

    @GetMapping("/schedule")
    public List<ClassSchedule> getStudentSchedule() {
        return feedbackService.getStudentSchedule();
    }

    @GetMapping("/get-subjects")
    public ResponseEntity<List<SubjectDto>> getAllSubjects(){
        return new ResponseEntity<>(feedbackService.getAllSubjects(),HttpStatus.OK);
    }
    @GetMapping("/subject/{studentId}")
    public ResponseEntity<SubjectDto> getSubjectById(@PathVariable Long subjectId){
        return new ResponseEntity<>(feedbackService.getSubjectById(subjectId),HttpStatus.OK);
    }

    @PostMapping("/add-session")
    public ResponseEntity<Session> createSession(@RequestBody SessionDto sessionDto){
        Session createdSession= feedbackService.createSession(sessionDto);
        return new ResponseEntity<>(createdSession,HttpStatus.CREATED);
    }
    @DeleteMapping("/cancel-session/{sessionId}")
    public ResponseEntity<?> cancelSession(@PathVariable Long sessionId) {
        try {
            feedbackService.cancelSession(sessionId);
            return ResponseEntity.ok("Session deleted successfully.");
        } catch (SubjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<AssignmentDto>> getStudentAssignments() {
            List<AssignmentDto> assignments = feedbackService.getStudentAssignments();
            if (CollectionUtils.isEmpty(assignments)) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

}
