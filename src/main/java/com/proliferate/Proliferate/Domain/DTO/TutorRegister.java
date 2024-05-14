package com.proliferate.Proliferate.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TutorRegister {

    private long userId;

    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private String contactNumber;

    private String password;

    private String gender;

    private int age;

    private String gradeYear;
}
