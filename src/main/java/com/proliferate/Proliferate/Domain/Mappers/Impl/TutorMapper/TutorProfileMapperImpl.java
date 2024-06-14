package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentProfile;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorProfile;
import com.proliferate.Proliferate.Domain.Entities.Feedback;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class TutorProfileMapperImpl implements Mapper<TutorEntity, TutorProfile> {

    private final ModelMapper modelMapper;

 @Override
    public TutorProfile mapTo(TutorEntity tutorEntity) {
        TutorProfile tutorProfile = new TutorProfile();
        Feedback feedback = new Feedback();
        tutorProfile.setFullName(tutorEntity.getFirstName() + " " + tutorEntity.getLastName());
        tutorProfile.setSubjectExpertise(tutorEntity.getPreferredSubjects().toString());
        tutorProfile.setQualification(tutorEntity.getHighestEducationLevelAttained());
		tutorProfile.setTeachingStyle(tutorEntity.getTeachingStyle());
		tutorProfile.setRating(feedback.getRating());
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
                .map(tutorEntity -> modelMapper.map(
                        tutorEntity, TutorProfile.class
                )).collect(Collectors.toList());
    }


}

