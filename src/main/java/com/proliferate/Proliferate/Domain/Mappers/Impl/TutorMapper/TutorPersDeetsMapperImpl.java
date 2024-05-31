package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorRegister;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class TutorPersDeetsMapperImpl implements Mapper<TutorEntity, TutorRegister> {

        private final ModelMapper modelMapper;

        @Override
        public TutorRegister mapTo(TutorEntity tutorEntity) {
            return modelMapper.map(tutorEntity, TutorRegister.class);
        }

        @Override
        public TutorEntity mapFrom(TutorRegister tutorRegister) {
            return modelMapper.map(tutorRegister,TutorEntity.class);
        }

        @Override
        public Iterable<TutorRegister> mapListTo(Iterable<TutorEntity> a) {
            return null;
        }

}
