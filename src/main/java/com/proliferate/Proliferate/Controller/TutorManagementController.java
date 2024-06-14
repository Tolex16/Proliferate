package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentProfile;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentTable;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorProfile;
import com.proliferate.Proliferate.Domain.Entities.Feedback;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.Service.FeedbackService;
import com.proliferate.Proliferate.Service.StudentManagementService;
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
    private final FeedbackService feedbackService;

    private final Mapper<TutorEntity, TutorProfile> tutorProfileMapper;

    @PostMapping("create-feedback")
    public Feedback saveFeedback(@RequestBody Feedback feedback) {
        return feedbackService.saveFeedback(feedback);
    }

    @GetMapping("/tutor/{tutorName}")
    public List<Feedback> getFeedbackByTutorName(@PathVariable String tutorName) {
        return feedbackService.getFeedbackByTutorName(tutorName);
    }

    @GetMapping("/tutor/{tutorName}/average")
    public double getAverageRating(@PathVariable String tutorName) {
        return feedbackService.getAverageRating(tutorName);
    }

    @GetMapping("/get-tutorProfile/{tutorId}")
    public ResponseEntity<TutorProfile> getStudentProfile(@PathVariable Long tutorId) {
        Optional<TutorEntity> tutorEntityOptional = feedbackService.getTutorProfile(tutorId);
        if (tutorEntityOptional.isPresent()) {
            TutorProfile tutorProfile = tutorProfileMapper.mapTo(tutorEntityOptional.get());
            return ResponseEntity.ok(tutorProfile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
