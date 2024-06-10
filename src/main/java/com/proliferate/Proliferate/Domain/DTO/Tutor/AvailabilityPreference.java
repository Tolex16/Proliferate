package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AvailabilityPreference {
    
	private List<String> preferredSubjects;
	
    private String studentAssessmentApproach;
    
	private String availableForAdditionalSupport;

}
