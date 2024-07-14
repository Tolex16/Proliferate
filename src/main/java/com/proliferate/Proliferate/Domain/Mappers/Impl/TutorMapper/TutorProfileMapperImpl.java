package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorProfile;
import com.proliferate.Proliferate.Domain.Entities.Feedback;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.Repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class TutorProfileMapperImpl implements Mapper<TutorEntity, TutorProfile> {

    private final ModelMapper modelMapper;
	
	private final FeedbackRepository feedbackRepository;

    @Override
    public TutorProfile mapTo(TutorEntity tutorEntity) {
        TutorProfile tutorProfile = new TutorProfile();
        tutorProfile.setFullName(tutorEntity.getFirstName() + " " + tutorEntity.getLastName());
        tutorProfile.setSubjectExpertise(tutorEntity.getPreferredSubjects());
        tutorProfile.setQualification(tutorEntity.getHighestEducationLevelAttained());
        tutorProfile.setTeachingStyle(tutorEntity.getTeachingStyle());

        if (tutorEntity.getTutorImage() != null) {
            tutorProfile.setProfileImage(Base64.getEncoder().encodeToString(tutorEntity.getTutorImage()));
        } else {
            tutorProfile.setProfileImage(null); // or set a default image, if applicable
        }

        List<Feedback> feedbacks = feedbackRepository.findByTutorTutorId(tutorEntity.getTutorId());
        double averageRating = feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0);
        tutorProfile.setRating(averageRating);

        tutorProfile.setBio(tutorEntity.getBio());
        return tutorProfile;
    }

    @Override
    public TutorEntity mapFrom(TutorProfile tutorProfile) {
        return modelMapper.map(tutorProfile, TutorEntity.class);
    }

    @Override
    public List<TutorProfile> mapListTo(Iterable<TutorEntity> tutorEntities) {
        return StreamSupport.stream(tutorEntities.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());
    }


}

