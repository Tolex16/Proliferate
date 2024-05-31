package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TeachingStyleApproach {

    private String teachingStyle;

    private String approachToTutoring;

    private String attendanceType;

}
