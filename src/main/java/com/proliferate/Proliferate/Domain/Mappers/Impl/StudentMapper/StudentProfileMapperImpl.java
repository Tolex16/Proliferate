package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentProfile;
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
public class StudentProfileMapperImpl implements Mapper<StudentEntity, StudentProfile> {

    private final ModelMapper modelMapper;

 @Override
    public StudentProfile mapTo(StudentEntity studentEntity) {
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setFullName(studentEntity.getFirstName() + " " + studentEntity.getLastName());
        studentProfile.setAge(studentEntity.getAge());
        studentProfile.setGradeYear(studentEntity.getGradeYear());
		studentProfile.setAttendanceType(studentEntity.getAttendanceType());
		studentProfile.setAvailability(studentEntity.getAvailability());
		studentProfile.setAdditionalPreferencesRequirements(studentEntity.getAdditionalPreferences() + ", " + studentEntity.getRequirements());
        studentProfile.setShortTermGoals(studentEntity.getShortTermGoals());
		studentProfile.setLongTermGoals(studentEntity.getLongTermGoals());
		return studentProfile;
    }

    @Override
    public StudentEntity mapFrom(StudentProfile studentProfile) {
        return modelMapper.map(studentProfile, StudentEntity.class);
    }

    @Override
    public List<StudentProfile> mapListTo(Iterable<StudentEntity> studentEntities) {
        return StreamSupport.stream(studentEntities.spliterator(), false)
                .map(studentEntity -> modelMapper.map(
                        studentEntity, StudentProfile.class
                )).collect(Collectors.toList());
    }


}

