package com.proliferate.Proliferate.Domain.DTO.Tutor;

import com.proliferate.Proliferate.Domain.DTO.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TutorProfile {

    private String fullName;

    private String subjectExpertise;

    private String qualification;
	
	private String teachingStyle;
	
    private double rating;

    private String profileImage;

    private String bio;

}
