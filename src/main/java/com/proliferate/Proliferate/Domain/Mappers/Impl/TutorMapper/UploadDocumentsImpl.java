package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;


import com.proliferate.Proliferate.Domain.DTO.Tutor.UploadDocuments;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class UploadDocumentsImpl implements Mapper<TutorEntity, UploadDocuments> {

    private final ModelMapper modelMapper;
    @Override
    public UploadDocuments mapTo(TutorEntity tutorEntity) {
        return modelMapper.map(tutorEntity, UploadDocuments.class);
    }

    @Override
    public TutorEntity mapFrom(UploadDocuments uploadDocuments) {
        return modelMapper.map(uploadDocuments,TutorEntity.class);
    }

    @Override
    public Iterable<UploadDocuments> mapListTo(Iterable<TutorEntity> a) {
        return null;
    }
}
