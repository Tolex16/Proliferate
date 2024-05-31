package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentRegisterPersDeets;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class StudentRegisterMapperImpl implements Mapper<StudentEntity, StudentRegisterPersDeets> {

    private final ModelMapper modelMapper;
    @Override
    public StudentRegisterPersDeets mapTo(StudentEntity studentEntity) {
        return modelMapper.map(studentEntity, StudentRegisterPersDeets.class);
    }

    @Override
    public StudentEntity mapFrom(StudentRegisterPersDeets studentRegister) {
        return modelMapper.map(studentRegister,StudentEntity.class);
    }

    @Override
    public Iterable<StudentRegisterPersDeets> mapListTo(Iterable<StudentEntity> a) {
        return null;
    }

}
