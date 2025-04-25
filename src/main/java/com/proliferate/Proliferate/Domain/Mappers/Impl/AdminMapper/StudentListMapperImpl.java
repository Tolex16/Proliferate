package com.proliferate.Proliferate.Domain.Mappers.Impl.AdminMapper;

import com.proliferate.Proliferate.Domain.DTO.Admin.StudentList;;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class StudentListMapperImpl implements Mapper<StudentEntity, StudentList> {

    private final ModelMapper modelMapper;

 @Override
    public StudentList mapTo(StudentEntity studentEntity) {
        StudentList studentList = new StudentList();
        studentList.setStudentId(studentEntity.getStudentId());
        studentList.setFullName(studentEntity.getFirstName() + " " + studentEntity.getLastName());
        studentList.setUsername(studentEntity.getUsername());
        studentList.setEmail(studentEntity.getEmail());
        studentList.setSubjectsNeedingTutoring(studentEntity.getSubjectsNeedingTutoring());
        studentList.setAge(studentEntity.getAge());
        studentList.setGradeYear(studentEntity.getGradeYear());

        return studentList;
    }

    @Override
    public StudentEntity mapFrom(StudentList studentList) {
        return modelMapper.map(studentList, StudentEntity.class);
    }

    @Override
    public Iterable<StudentList> mapListTo(Iterable<StudentEntity> studentEntities) {
        return StreamSupport.stream(studentEntities.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());
    }



}

