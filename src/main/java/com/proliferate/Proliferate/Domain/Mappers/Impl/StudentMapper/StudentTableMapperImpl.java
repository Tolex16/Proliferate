package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentTable;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class StudentTableMapperImpl implements Mapper<StudentEntity, StudentTable> {

    private final ModelMapper modelMapper;

 @Override
    public StudentTable mapTo(StudentEntity studentEntity) {
        StudentTable studentTable = new StudentTable();
        studentTable.setStudentId(studentEntity.getStudentId());
        studentTable.setFullName(studentEntity.getFirstName() + " " + studentEntity.getLastName());
        studentTable.setSubjectsNeedingTutoring(studentEntity.getSubjectsNeedingTutoring());
        studentTable.setAge(studentEntity.getAge());
        studentTable.setGradeYear(studentEntity.getGradeYear());
        studentTable.setAttendanceType(studentEntity.getAttendanceType());
        studentTable.setAvailability(studentEntity.getAvailability());
        studentTable.setAdditionalPreferencesRequirements(studentEntity.getAdditionalPreferences() + ", " + studentEntity.getRequirements());
        studentTable.setShortTermGoals(studentEntity.getShortTermGoals());
        studentTable.setLongTermGoals(studentEntity.getLongTermGoals());
        return studentTable;
    }

    @Override
    public StudentEntity mapFrom(StudentTable studentTable) {
        return modelMapper.map(studentTable, StudentEntity.class);
    }

    @Override
    public Iterable<StudentTable> mapListTo(Iterable<StudentEntity> studentEntities) {
        return StreamSupport.stream(studentEntities.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());
    }



}

