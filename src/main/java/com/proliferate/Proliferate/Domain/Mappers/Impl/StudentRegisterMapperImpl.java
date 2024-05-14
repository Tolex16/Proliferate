package com.proliferate.Proliferate.Domain.Mappers.Impl;

import com.proliferate.Proliferate.Domain.DTO.StudentRegisterPersDeets;
import com.proliferate.Proliferate.Domain.Entities.UserEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class StudentRegisterMapperImpl implements Mapper<UserEntity, StudentRegisterPersDeets> {

    private final ModelMapper modelMapper;
    @Override
    public StudentRegisterPersDeets mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, StudentRegisterPersDeets.class);
    }

    @Override
    public UserEntity mapFrom(StudentRegisterPersDeets studentRegister) {
        return modelMapper.map(studentRegister,UserEntity.class);
    }

    @Override
    public Iterable<StudentRegisterPersDeets> mapListTo(Iterable<UserEntity> a) {
        return null;
    }

}
