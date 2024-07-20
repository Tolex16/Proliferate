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

    private int age;

    private String gradeYear;

    private String attendanceType;

    private String availability;

    private String additionalPreferencesRequirements;

    private String shortTermGoals;

    private String longTermGoals;

    private String profileImage;

    private String bio;
   
}
