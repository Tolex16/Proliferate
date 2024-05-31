package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.Preferences;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class PreferencesMapperImpl implements Mapper<StudentEntity, Preferences> {

    private final ModelMapper modelMapper;
    @Override
    public Preferences mapTo(StudentEntity userEntity) {
        return modelMapper.map(userEntity, Preferences.class);
    }

    @Override
    public StudentEntity mapFrom(Preferences preferences) {
        return modelMapper.map(preferences,StudentEntity.class);
    }

    @Override
    public Iterable<Preferences> mapListTo(Iterable<StudentEntity> a) {
        return null;
    }
}
