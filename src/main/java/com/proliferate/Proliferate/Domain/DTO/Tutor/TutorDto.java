package com.proliferate.Proliferate.Domain.DTO.Tutor;

import com.proliferate.Proliferate.Domain.DTO.Gender;
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

    private String email;

    private String contactNumber;

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

    private String studentAssessmentApproach;

    private String availableForAdditionalSupport;

    private boolean termsAndConditionsApproved;

    private boolean registrationCompleted;
}
