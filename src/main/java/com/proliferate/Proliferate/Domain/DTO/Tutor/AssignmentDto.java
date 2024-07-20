package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AssignmentDto {
	private Long assignmentId;
	
    private String assignedStudentName;

    private Long assignedStudentId;
	
    private String dueDate;
	
    private String title;
	
    private String subjectName;
	
    private String gradeLevel;

    private String description;
	
    private MultipartFile assignmentFile;

	private String assignmentFileBase64; // Add this field to hold the base64 string

    private MultipartFile assignmentSolution;

    private String assignmentSolutionBase64;
	
	
	public void setDueDate(LocalDate dueDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.dueDate = dueDate.format(formatter);
    }
}
