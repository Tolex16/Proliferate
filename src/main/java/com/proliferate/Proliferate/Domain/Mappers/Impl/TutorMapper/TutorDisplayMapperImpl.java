package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorDisplay;
import com.proliferate.Proliferate.Domain.Entities.Feedback;
import com.proliferate.Proliferate.Domain.Entities.Subject;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.Repository.FeedbackRepository;
import com.proliferate.Proliferate.Repository.SubjectRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class TutorDisplayMapperImpl implements Mapper<TutorEntity, TutorDisplay> {
    private final ModelMapper modelMapper;

    private final FeedbackRepository feedbackRepository;

    private final TutorRepository tutorRepository;
    private final SubjectRepository subjectRepository;
    @Override
    public TutorDisplay mapTo(TutorEntity tutor) {
        TutorDisplay tutorDisplay = new TutorDisplay();
        if (tutor.getTutorImage() != null) {
            String base64File = Base64.getEncoder().encodeToString(tutor.getTutorImage());
            tutorDisplay.setProfileImage(base64File);
        }

        tutorDisplay.setFullName(tutor.getFirstName() + " " + tutor.getLastName());

        Optional<Feedback> feedbacks = feedbackRepository.findById(tutor.getTutorId());
        double averageRating = feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0);
        tutorDisplay.setReview(averageRating);
       Optional<Subject> subjectOpt = subjectRepository.findByTutorTutorId(tutor.getTutorId());

        tutorDisplay.setSubject(subjectOpt.get().getTitle());
        tutorDisplay.setBio(tutor.getBio());

        return tutorDisplay;
    }

    @Override
    public TutorEntity mapFrom(TutorDisplay tutorDisplay) {
        return modelMapper.map(tutorDisplay, TutorEntity.class);
    }

    @Override
    public Iterable<TutorDisplay> mapListTo(Iterable<TutorEntity> tutorEntities) {
        return StreamSupport.stream(tutorEntities.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
