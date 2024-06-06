package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;


import com.proliferate.Proliferate.Domain.DTO.Tutor.UpdateTutor;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class UpdateTutorMapperImpl implements Mapper<TutorEntity, UpdateTutor> {

    private final ModelMapper modelMapper;

    @Override
    public UpdateTutor mapTo(TutorEntity tutorEntity) {
        return modelMapper.map(tutorEntity, UpdateTutor.class);
    }

    @Override
    public TutorEntity mapFrom(UpdateTutor updateTutor) {
        return modelMapper.map(updateTutor,TutorEntity.class);
    }

    @Override
    public Iterable<UpdateTutor> mapListTo(Iterable<TutorEntity> a) {
        return null;
    }
}
