package com.proliferate.Proliferate.Domain.DTO.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StudentDisplay {

    private String profileImage; // Add this field to hold the base64 string

    private String fullName;

    private String bio;
}
