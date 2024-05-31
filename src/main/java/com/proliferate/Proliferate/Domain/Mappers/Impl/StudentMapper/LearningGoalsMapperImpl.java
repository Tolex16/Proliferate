package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.LearningGoals;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class LearningGoalsMapperImpl implements Mapper<StudentEntity, LearningGoals> {

    private final ModelMapper modelMapper;

    @Override
    public LearningGoals mapTo(StudentEntity studentEntity) {
        return modelMapper.map(studentEntity, LearningGoals.class);
    }

    @Override
    public StudentEntity mapFrom(LearningGoals learningGoals) {
        return modelMapper.map(learningGoals,StudentEntity.class);
    }

    @Override
    public Iterable<LearningGoals> mapListTo(Iterable<StudentEntity> a) {
        return null;
    }
}
