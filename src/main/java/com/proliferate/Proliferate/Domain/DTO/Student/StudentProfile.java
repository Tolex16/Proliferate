package com.proliferate.Proliferate.Domain.DTO.Student;

import com.proliferate.Proliferate.Domain.DTO.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StudentProfile {

    private String fullName;

    private int age;

    private String gradeYear;
	
	private String attendanceType;
	
    private String availability;

    private String additionalPreferencesRequirements;

    private String shortTermGoals;

    private String longTermGoals;

}
