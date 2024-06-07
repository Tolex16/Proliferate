package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;


import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.Assignment;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@Component
public class AssignmentMapperImpl implements Mapper<Assignment, AssignmentDto> {

    private final ModelMapper modelMapper;

    @Override
    public AssignmentDto mapTo(Assignment assignment) {
        return modelMapper.map(assignment, AssignmentDto.class);
    }

    @Override
    public Assignment mapFrom(AssignmentDto assignmentDto) {
        return modelMapper.map(assignmentDto,Assignment.class);
    }

    @Override
    public Iterable<AssignmentDto> mapListTo(Iterable<Assignment> a) {
        return null;
    }
}
