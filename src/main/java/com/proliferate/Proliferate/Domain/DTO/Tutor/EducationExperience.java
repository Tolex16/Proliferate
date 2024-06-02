package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EducationExperience {

    private String highestEducationLevelAttained;

    private String majorFieldOfStudy;

    private String yearsOfTeachingExperience;

    private String teachingGrade;

    private String currentSchool;

    private String location;
}
