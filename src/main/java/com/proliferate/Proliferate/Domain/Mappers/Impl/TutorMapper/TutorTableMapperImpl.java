package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorProfile;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorTable;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
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
public class TutorTableMapperImpl implements Mapper<TutorEntity, TutorTable> {

    private final ModelMapper modelMapper;

    @Override
    public TutorTable mapTo(TutorEntity tutor) {
        TutorTable tutorTable = new TutorTable();
        tutorTable.setTutorId(tutor.getTutorId());
        tutorTable.setFullName(tutor.getFirstName() + " " + tutor.getLastName());
		if (tutor.getTutorImage() != null) {
            tutorTable.setProfileImage(Base64.getEncoder().encodeToString(tutor.getTutorImage()));;
        } else {
            tutorTable.setProfileImage(null); // or set a default image, if applicable
        }

        return tutorTable;
    }

    @Override
    public TutorEntity mapFrom(TutorTable tutorTable) {
        return modelMapper.map(tutorTable, TutorEntity.class);
    }

    @Override
    public Iterable<TutorTable> mapListTo(Iterable<TutorEntity> tutorEntities) {
        return StreamSupport.stream(tutorEntities.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
