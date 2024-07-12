package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentDisplay;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorDisplay;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class StudentDisplayMapperImpl implements Mapper<StudentEntity, StudentDisplay> {
private final ModelMapper modelMapper;

    @Override
    public StudentDisplay mapTo(StudentEntity student) {
        StudentDisplay studentDisplay = new StudentDisplay();
        if (student.getStudentImage() != null) {
            String base64File = Base64.getEncoder().encodeToString(student.getStudentImage());
            studentDisplay.setStudentImage(base64File);
        }

        studentDisplay.setBio(student.getBio());

        return studentDisplay;
    }

    @Override
    public StudentEntity mapFrom(StudentDisplay studentDisplay) {
        return modelMapper.map(studentDisplay, StudentEntity.class);
    }

    @Override
    public Iterable<StudentDisplay> mapListTo(Iterable<StudentEntity> studentEntities) {
        return StreamSupport.stream(studentEntities.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
