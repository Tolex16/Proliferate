package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TutorProfile {

    private String fullName;

    private List<String> subjectExpertise;

    private String qualification;
	
	private String teachingStyle;
	
    private double rating;

    private String profileImage;

    private String bio;

}
