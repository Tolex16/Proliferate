package com.proliferate.Proliferate.Domain.DTO.Student;

import com.proliferate.Proliferate.Domain.DTO.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StudentRegisterPersDeets {

    private long studentId;

    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private String contactNumber;

    private String password;

    private Gender gender;

    private int age;

}
