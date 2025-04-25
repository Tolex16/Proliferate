package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.UpdateStudent;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class UpdateStudentMapperImpl implements Mapper<StudentEntity, UpdateStudent> {

    private final ModelMapper modelMapper;

    @Override
    public UpdateStudent mapTo(StudentEntity studentEntity) {
        return modelMapper.map(studentEntity, UpdateStudent.class);
    }

    @Override
    public StudentEntity mapFrom(UpdateStudent updateStudent) {
        return modelMapper.map(updateStudent, StudentEntity.class);
    }

    @Override
    public Iterable<UpdateStudent> mapListTo(Iterable<StudentEntity> a) {
        return null;
    }
}
