package com.proliferate.Proliferate.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AcademicDetail {

    private String gradeYear;

    private String subjectsNeedingTutoring;

    private String attendanceType;

    private String currentLocation;
}
