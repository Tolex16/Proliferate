package com.proliferate.Proliferate.Domain.DTO.Student;

import com.proliferate.Proliferate.Domain.DTO.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StudentDto {
    private long studentId;

    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private String contactNumber;

    private String password;

    private Gender gender;

    private int age;

    private String gradeYear;

    private String subjectsNeedingTutoring;

    private String attendanceType;

    private String currentLocation;

    private String availability;

    private String additionalPreferences;

    private String requirements;

    private String communicationLanguage;

    private String shortTermGoals;

    private String longTermGoals;

    private boolean termsAndConditionsApproved;

    private boolean registrationCompleted;
}
