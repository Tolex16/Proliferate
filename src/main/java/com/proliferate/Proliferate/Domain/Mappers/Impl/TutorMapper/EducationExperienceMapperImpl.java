package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Tutor.EducationExperience;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class EducationExperienceMapperImpl implements Mapper<TutorEntity, EducationExperience> {

    private final ModelMapper modelMapper;
    @Override
    public EducationExperience mapTo(TutorEntity tutorEntity) {
        return modelMapper.map(tutorEntity, EducationExperience.class);
    }

    @Override
    public TutorEntity mapFrom(EducationExperience educationExperience) {
        return modelMapper.map(educationExperience,TutorEntity.class);
    }

    @Override
    public Iterable<EducationExperience> mapListTo(Iterable<TutorEntity> a) {
        return null;
    }
}
