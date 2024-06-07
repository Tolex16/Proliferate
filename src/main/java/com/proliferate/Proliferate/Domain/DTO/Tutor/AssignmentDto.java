package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AssignmentDto {
	
    private List<String> assignedStudent;
	
    private String dueDate;
	
    private String title;
	
    private String subject;
	
    private String gradeLevel;
	
    private MultipartFile assignmentFile;
}
