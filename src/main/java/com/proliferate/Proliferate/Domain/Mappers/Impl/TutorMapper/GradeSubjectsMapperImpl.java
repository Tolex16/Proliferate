package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;


import com.proliferate.Proliferate.Domain.DTO.Tutor.GradeSubjects;
import com.proliferate.Proliferate.Domain.DTO.Tutor.UpdateTutor;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class GradeSubjectsMapperImpl implements Mapper<TutorEntity, GradeSubjects> {

    private final ModelMapper modelMapper;

    @Override
    public GradeSubjects mapTo(TutorEntity tutorEntity) {
        return modelMapper.map(tutorEntity, GradeSubjects.class);
    }

    @Override
    public TutorEntity mapFrom(GradeSubjects gradeSubjects) {
        return modelMapper.map(gradeSubjects,TutorEntity.class);
    }

    @Override
    public Iterable<GradeSubjects> mapListTo(Iterable<TutorEntity> a) {
        return null;
    }
}
