package com.proliferate.Proliferate.Domain.DTO.Student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proliferate.Proliferate.Domain.DTO.Gender;
import jakarta.validation.constraints.Email;
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

    @Email(message = "Email should be valid")
    private String email;

    private String contactNumber;

    @JsonIgnore
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
