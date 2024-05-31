package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;


import com.proliferate.Proliferate.Domain.DTO.Student.AcademicDetail;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class AcademicDetailMapperImpl implements Mapper<StudentEntity, AcademicDetail> {

    private final ModelMapper modelMapper;
    @Override
    public AcademicDetail mapTo(StudentEntity studentEntity) {
        return modelMapper.map(studentEntity, AcademicDetail.class);
    }

    @Override
    public StudentEntity mapFrom(AcademicDetail academicDetail) {
        return modelMapper.map(academicDetail,StudentEntity.class);
    }

    @Override
    public Iterable<AcademicDetail> mapListTo(Iterable<StudentEntity> a) {
        return null;
    }
}
