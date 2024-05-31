package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Tutor.TeachingStyleApproach;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class TeachingStyleApproachMapperImpl implements Mapper<TutorEntity, TeachingStyleApproach> {

    private final ModelMapper modelMapper;
    @Override
    public TeachingStyleApproach mapTo(TutorEntity tutorEntity) {
        return modelMapper.map(tutorEntity, TeachingStyleApproach.class);
    }

    @Override
    public TutorEntity mapFrom(TeachingStyleApproach teachingStyleAppproach) {
        return modelMapper.map(teachingStyleAppproach, TutorEntity.class);
    }

    @Override
    public Iterable<TeachingStyleApproach> mapListTo(Iterable<TutorEntity> a) {
        return null;
    }
}
