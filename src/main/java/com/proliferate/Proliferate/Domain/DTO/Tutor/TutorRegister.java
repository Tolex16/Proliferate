package com.proliferate.Proliferate.Domain.DTO.Tutor;

import com.proliferate.Proliferate.Domain.DTO.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TutorRegister {

    private long tutorId;

    private String firstName;

    private String lastName;

    private String email;

    private String contactNumber;

    private String password;

    private Gender gender;

    private int age;
}