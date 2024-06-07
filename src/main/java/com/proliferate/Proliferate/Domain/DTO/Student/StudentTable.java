package com.proliferate.Proliferate.Domain.DTO.Student;

import com.proliferate.Proliferate.Domain.DTO.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StudentTable {
    private long studentId;

    private String fullName;

    private String subjectsNeedingTutoring;
	
	private String gradeYear;

   
}
