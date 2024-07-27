package com.proliferate.Proliferate.Domain.DTO.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StudentList {

    private long studentId;

    private String fullName;

    private String username;

    private String email;

    private String subjectsNeedingTutoring;

    private int age;

    private String gradeYear;

}
