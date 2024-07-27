package com.proliferate.Proliferate.Domain.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Schedule {

	private Long classScheduleId;

    //private Long studentId;
	
	private Long tutorId;
	
	private String date; 
	
	private Long subjectId;
	
	private String subjectTitle;
	
	private String tutorName;

	private String studentName;
	
	private String time; 
	
	private String location; 
	
	private String reason;

	private String schedule;
	
	private double rating;

	private String duration;
}
