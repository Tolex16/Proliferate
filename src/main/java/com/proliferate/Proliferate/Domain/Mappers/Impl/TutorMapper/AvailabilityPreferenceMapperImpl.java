package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;


import com.proliferate.Proliferate.Domain.DTO.Tutor.AvailabilityPreference;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class AvailabilityPreferenceMapperImpl implements Mapper<TutorEntity, AvailabilityPreference> {

    private final ModelMapper modelMapper;
    @Override
    public AvailabilityPreference mapTo(TutorEntity tutorEntity) {
        return modelMapper.map(tutorEntity, AvailabilityPreference.class);
    }

    @Override
    public TutorEntity mapFrom(AvailabilityPreference availabilityPreference) {
        return modelMapper.map(availabilityPreference,TutorEntity.class);
    }

    @Override
    public Iterable<AvailabilityPreference> mapListTo(Iterable<TutorEntity> a) {
        return null;
    }
}
