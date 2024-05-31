package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorDto;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class TutorMapperImpl implements Mapper<TutorEntity, TutorDto> {

    private final ModelMapper modelMapper;

    @Override
    public TutorDto mapTo(TutorEntity tutorEntity) {
        return modelMapper.map(tutorEntity, TutorDto.class);
    }

    @Override
    public TutorEntity mapFrom(TutorDto tutorDto) {
        return modelMapper.map(tutorDto,TutorEntity.class);
    }

    @Override
    public Iterable<TutorDto> mapListTo(Iterable<TutorEntity> tutorEntities) {
        return StreamSupport.stream(tutorEntities.spliterator(), false)
                .map(tutorEntity -> modelMapper.map(
                        tutorEntity, TutorDto.class
                )).collect(Collectors.toList());
    }


}

