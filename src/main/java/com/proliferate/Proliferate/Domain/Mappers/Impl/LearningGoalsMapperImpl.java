package com.proliferate.Proliferate.Domain.Mappers.Impl;

import com.proliferate.Proliferate.Domain.DTO.LearningGoals;
import com.proliferate.Proliferate.Domain.DTO.Preferences;
import com.proliferate.Proliferate.Domain.Entities.UserEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class LearningGoalsMapperImpl implements Mapper<UserEntity, LearningGoals> {

    private final ModelMapper modelMapper;

    @Override
    public LearningGoals mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, LearningGoals.class);
    }

    @Override
    public UserEntity mapFrom(LearningGoals learningGoals) {
        return modelMapper.map(learningGoals,UserEntity.class);
    }

    @Override
    public Iterable<LearningGoals> mapListTo(Iterable<UserEntity> a) {
        return null;
    }
}
