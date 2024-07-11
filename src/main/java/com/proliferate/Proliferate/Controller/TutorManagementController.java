package com.proliferate.Proliferate.Controller;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentTable;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.FeedbackDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorProfile;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorTable;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.Service.TutorManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Iterable<TutorEntity> tutors = feedbackService.getAllTutors();
        Iterable<TutorTable> allTutors = tutorTableMapper.mapListTo(tutors);
        return ResponseEntity.ok(allTutors);
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

	@GetMapping("/scores/{studentId}")
    public ResponseEntity<List<Score>> getStudentScores(@PathVariable Long studentId) {
        List<Score> scores = feedbackService.getStudentScores(studentId);
        return ResponseEntity.ok(scores);
    }

    @PostMapping("/create-class-schedule")
    public ClassSchedule createClassSchedule(@RequestBody Schedule schedule) {
        return feedbackService.createClassSchedule(schedule);
    }

    @GetMapping("/schedule/{studentId}")
    public List<ClassSchedule> getStudentSchedule(@PathVariable Long studentId) {
        return feedbackService.getStudentSchedule(studentId);
    }

    @PostMapping("/add-subject")
    public ResponseEntity<Subject> createSubject(@RequestBody SubjectDto subjectDto){
        Subject createdSubject = feedbackService.createSubject(subjectDto);
        return new ResponseEntity<>(createdSubject,HttpStatus.CREATED);
    }
    @GetMapping("/get-subjects")
    public ResponseEntity<List<SubjectDto>> getAllSubjects(){
        return new ResponseEntity<>(feedbackService.getAllSubjects(),HttpStatus.OK);
    }
    @GetMapping("/subject/{studentId}")
    public ResponseEntity<SubjectDto> getSubjectById(@PathVariable Long subjectId){
        return new ResponseEntity<>(feedbackService.getSubjectById(subjectId),HttpStatus.OK);
    }

    @DeleteMapping("/subject/delete/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long subjectId) {
        try {
            feedbackService.deleteSubject(subjectId);
            return ResponseEntity.ok("Subject deleted successfully.");
        } catch (SubjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/assignments/{studentId}")
    public ResponseEntity<List<AssignmentDto>> getStudentAssignments(@PathVariable Long studentId) {
            List<AssignmentDto> assignments = feedbackService.getStudentAssignments(studentId);
            if (CollectionUtils.isEmpty(assignments)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

}
