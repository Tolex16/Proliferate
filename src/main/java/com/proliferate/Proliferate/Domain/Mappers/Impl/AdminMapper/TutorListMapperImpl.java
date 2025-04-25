package com.proliferate.Proliferate.Domain.Mappers.Impl.AdminMapper;

import com.proliferate.Proliferate.Domain.DTO.Admin.TutorList;
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
public class TutorListMapperImpl implements Mapper<TutorEntity, TutorList> {

    private final ModelMapper modelMapper;

    @Override
    public TutorList mapTo(TutorEntity tutor) {
        TutorList tutorList = new TutorList();
        tutorList.setTutorId(tutor.getTutorId());
        tutorList.setFullName(tutor.getFirstName() + " " + tutor.getLastName());
		tutorList.setEmail(tutor.getEmail());
        tutorList.setAvailability(tutor.getWeeklyAvailability());
        tutorList.setBio(tutor.getBio());
        return tutorList;
    }

    @Override
    public TutorEntity mapFrom(TutorList tutorList) {
        return modelMapper.map(tutorList, TutorEntity.class);
    }

    @Override
    public Iterable<TutorList> mapListTo(Iterable<TutorEntity> tutorEntities) {
        return StreamSupport.stream(tutorEntities.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
