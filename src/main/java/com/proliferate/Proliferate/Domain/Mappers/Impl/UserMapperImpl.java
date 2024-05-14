package com.proliferate.Proliferate.Domain.Mappers.Impl;

import com.proliferate.Proliferate.Domain.DTO.UserDto;
import com.proliferate.Proliferate.Domain.Entities.UserEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

    private final ModelMapper modelMapper;

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        return modelMapper.map(userDto,UserEntity.class);
    }

    @Override
    public Iterable<UserDto> mapListTo(Iterable<UserEntity> userEntities) {
        return StreamSupport.stream(userEntities.spliterator(), false)
                .map(userEntity -> modelMapper.map(
                        userEntity, UserDto.class
                )).collect(Collectors.toList());
    }


}

