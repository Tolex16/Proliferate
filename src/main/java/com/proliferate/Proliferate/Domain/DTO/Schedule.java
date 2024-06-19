package com.proliferate.Proliferate.Domain.DTO;

import com.proliferate.Proliferate.config.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Schedule {

    private Long studentId; 
	
	private Long tutorId;
	
	private String date; 
	
	private Long subjectId;
	
	private String time; 
	
	private String location; 

}
