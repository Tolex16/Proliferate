package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Student.FriendInvite;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.UserAlreadyExistsException;
import com.proliferate.Proliferate.Repository.FeedbackRepository;
import com.proliferate.Proliferate.Repository.InvitationEmailRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Service.EmailService;
import com.proliferate.Proliferate.Service.FeedbackService;
import com.proliferate.Proliferate.Service.InviteService;
import com.proliferate.Proliferate.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    private final TutorRepository tutorRepository;

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackByTutorName(String tutorName) {
        return feedbackRepository.findByTutorName(tutorName);
    }

    public double getAverageRating(String tutorName) {
        List<Feedback> feedbacks = getFeedbackByTutorName(tutorName);
        return feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0);
    }

    public Optional<TutorEntity> getTutorProfile(Long tutorId) {
        return tutorRepository.findById(tutorId);
    }

}
