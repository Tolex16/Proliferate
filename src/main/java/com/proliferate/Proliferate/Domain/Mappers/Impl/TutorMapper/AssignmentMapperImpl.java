package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;


import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.Assignment;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.Subject;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.SubjectRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@Component
public class AssignmentMapperImpl implements Mapper<Assignment, AssignmentDto> {

    private final ModelMapper modelMapper;
    private final StudentRepository studentRepository;

    private final SubjectRepository subjectRepository;

    @Override
    public AssignmentDto mapTo(Assignment assignment) {
        AssignmentDto dto = new AssignmentDto();
        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setDueDate(assignment.getDueDate());
        dto.setTitle(assignment.getTitle());
        dto.setSubjectName(assignment.getSubject().getTitle());
        dto.setGradeLevel(assignment.getGradeLevel());
        dto.setDescription(assignment.getDescription());

        if (assignment.getAssignedStudent() != null) {
            dto.setAssignedStudentName(assignment.getAssignedStudent().getFirstName());
        }
        if (assignment.getAssignedStudent() != null) {
            dto.setAssignedStudentId(assignment.getAssignedStudent().getStudentId());
        }

        return dto;
    }


    @Override
    public Assignment mapFrom(AssignmentDto assignmentDto) {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentDto.getAssignmentId());
        assignment.setDueDate(LocalDate.parse(assignmentDto.getDueDate()));
        assignment.setTitle(assignmentDto.getTitle());
        assignment.setGradeLevel(assignmentDto.getGradeLevel());
        assignment.setDescription(assignmentDto.getDescription());
        
        // Fetch and set the student entity
        if (assignmentDto.getAssignedStudentName() != null) {
            StudentEntity student = studentRepository.findByFirstName(assignmentDto.getAssignedStudentName())
                    .orElseThrow(() -> new UserNotFoundException("Student not found"));
            assignment.setAssignedStudent(student);
        }
        if (assignmentDto.getSubjectName() != null) {
            Subject subject = subjectRepository.findByTitle(assignmentDto.getSubjectName())
                    .orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
            assignment.setSubject(subject);
        }
        return assignment;
    }

    @Override
    public Iterable<AssignmentDto> mapListTo(Iterable<Assignment> a) {
        return null;
    }
}
