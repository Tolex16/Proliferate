package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Student.FriendInvite;
import com.proliferate.Proliferate.Domain.Entities.Feedback;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    Feedback saveFeedback(Feedback feedback);

    List<Feedback> getFeedbackByTutorName(String tutorName);

    double getAverageRating(String tutorName);

    Optional<TutorEntity> getTutorProfile(Long tutorId);
}
