package com.proliferate.Proliferate.Domain.Mappers.Impl;

import com.proliferate.Proliferate.Domain.DTO.StudentRegisterPersDeets;
import com.proliferate.Proliferate.Domain.DTO.TutorRegister;
import com.proliferate.Proliferate.Domain.Entities.UserEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class TutorPersDeetsMapperImpl implements Mapper<UserEntity, TutorRegister> {

        private final ModelMapper modelMapper;

        @Override
        public TutorRegister mapTo(UserEntity userEntity) {
            return modelMapper.map(userEntity, TutorRegister.class);
        }

        @Override
        public UserEntity mapFrom(TutorRegister tutorRegister) {
            return modelMapper.map(tutorRegister,UserEntity.class);
        }

        @Override
        public Iterable<TutorRegister> mapListTo(Iterable<UserEntity> a) {
            return null;
        }

}
