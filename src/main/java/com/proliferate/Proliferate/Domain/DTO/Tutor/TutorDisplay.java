package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TutorDisplay {

    private String profileImage; // Add this field to hold the base64 string

    private String fullName;

    private String bio;

    private String subject;

    private double ratings;

    private int students;
	
	private int reviews; // Field to hold the number of feedbacks
}
