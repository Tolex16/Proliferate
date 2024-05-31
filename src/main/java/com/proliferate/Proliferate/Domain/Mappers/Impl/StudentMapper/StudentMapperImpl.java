package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentDto;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class StudentMapperImpl implements Mapper<StudentEntity, StudentDto> {

    private final ModelMapper modelMapper;

    @Override
    public StudentDto mapTo(StudentEntity studentEntity) {
        return modelMapper.map(studentEntity, StudentDto.class);
    }

    @Override
    public StudentEntity mapFrom(StudentDto studentDto) {
        return modelMapper.map(studentDto,StudentEntity.class);
    }

    @Override
    public Iterable<StudentDto> mapListTo(Iterable<StudentEntity> studentEntities) {
        return StreamSupport.stream(studentEntities.spliterator(), false)
                .map(studentEntity -> modelMapper.map(
                        studentEntity, StudentDto.class
                )).collect(Collectors.toList());
    }


}

