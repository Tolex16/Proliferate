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
	private Long id;
	
    private String assignedStudentName;
	
    private String dueDate;
	
    private String title;
	
    private String subjectName;
	
    private String gradeLevel;
	
    private MultipartFile assignmentFile;
	
	private String assignmentFileBase64; // Add this field to hold the base64 string
}
