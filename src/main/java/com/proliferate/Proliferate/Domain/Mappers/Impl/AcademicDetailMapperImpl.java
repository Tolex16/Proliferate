package com.proliferate.Proliferate.Domain.Mappers.Impl;

import com.proliferate.Proliferate.Domain.DTO.AcademicDetail;
import com.proliferate.Proliferate.Domain.DTO.StudentRegisterPersDeets;
import com.proliferate.Proliferate.Domain.DTO.TutorRegister;
import com.proliferate.Proliferate.Domain.Entities.UserEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class AcademicDetailMapperImpl implements Mapper<UserEntity, AcademicDetail> {

    private final ModelMapper modelMapper;
    @Override
    public AcademicDetail mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, AcademicDetail.class);
    }

    @Override
    public UserEntity mapFrom(AcademicDetail academicDetail) {
        return modelMapper.map(academicDetail,UserEntity.class);
    }

    @Override
    public Iterable<AcademicDetail> mapListTo(Iterable<UserEntity> a) {
        return null;
    }
}
