package com.proliferate.Proliferate.Domain.DTO.Student;

import com.proliferate.Proliferate.Domain.DTO.PreferredTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AcademicDetail {

    private String gradeYear;

    private String subjectsNeedingTutoring;

    private String attendanceType;
	
	private PreferredTime preferredTime;

    private String currentLocation;
}
