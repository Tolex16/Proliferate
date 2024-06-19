package com.proliferate.Proliferate.Domain.DTO.Tutor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proliferate.Proliferate.Domain.DTO.Gender;
import com.proliferate.Proliferate.config.StrongPassword;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TutorDto {
    private long tutorId;

    private String firstName;

    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    private String contactNumber;

    @StrongPassword
    @JsonIgnore
    private String password;

    private Gender gender;

    private int age;

	private String highestEducationLevelAttained;

    private String majorFieldOfStudy;

    private String yearsOfTeachingExperience;
	
    private String teachingGrade;
	
    private String currentSchool;
	
    private String location;
	
    private String teachingStyle;
	
    private String approachToTutoring;
	
    private String attendanceType;
	
	private List<String> preferredSubjects;

    private String weeklyAvailability;

    private String timeslotAvailability;

    private String selectTimezone;

    private String communicationLanguage;

    private boolean termsAndConditionsApproved;

    private boolean registrationCompleted;
}
