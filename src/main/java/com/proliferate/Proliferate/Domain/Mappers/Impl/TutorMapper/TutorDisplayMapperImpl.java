package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorDisplay;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class TutorDisplayMapperImpl implements Mapper<TutorEntity, TutorDisplay> {
    private final ModelMapper modelMapper;

    @Override
    public TutorDisplay mapTo(TutorEntity tutor) {
        TutorDisplay tutorDisplay = new TutorDisplay();
        if (tutor.getTutorImage() != null) {
            String base64File = Base64.getEncoder().encodeToString(tutor.getTutorImage());
            tutorDisplay.setTutorImage(base64File);
        }

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
