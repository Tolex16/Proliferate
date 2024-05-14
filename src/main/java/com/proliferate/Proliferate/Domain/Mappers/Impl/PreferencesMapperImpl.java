package com.proliferate.Proliferate.Domain.Mappers.Impl;

import com.proliferate.Proliferate.Domain.DTO.AcademicDetail;
import com.proliferate.Proliferate.Domain.DTO.Preferences;
import com.proliferate.Proliferate.Domain.DTO.StudentRegisterPersDeets;
import com.proliferate.Proliferate.Domain.Entities.UserEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class PreferencesMapperImpl implements Mapper<UserEntity, Preferences> {

    private final ModelMapper modelMapper;
    @Override
    public Preferences mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, Preferences.class);
    }

    @Override
    public UserEntity mapFrom(Preferences preferences) {
        return modelMapper.map(preferences,UserEntity.class);
    }

    @Override
    public Iterable<Preferences> mapListTo(Iterable<UserEntity> a) {
        return null;
    }
}
