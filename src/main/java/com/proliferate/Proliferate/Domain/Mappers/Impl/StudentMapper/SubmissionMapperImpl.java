package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.Submission;;
import com.proliferate.Proliferate.Domain.Entities.Assignment;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Data
@Component
public class SubmissionMapperImpl implements Mapper<Assignment, Submission> {

    private final ModelMapper modelMapper;

    @Override
    public Submission mapTo(Assignment assignment) {
        return modelMapper.map(assignment, Submission.class);
    }

    @Override
    public Assignment mapFrom(Submission submission) {
        return modelMapper.map(submission, Assignment.class);
    }

    @Override
    public Iterable<Submission> mapListTo(Iterable<Assignment> assignments) {
        return StreamSupport.stream(assignments.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
