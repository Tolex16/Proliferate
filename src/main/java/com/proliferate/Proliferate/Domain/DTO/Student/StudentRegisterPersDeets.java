package com.proliferate.Proliferate.Domain.DTO.Student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proliferate.Proliferate.Domain.DTO.Gender;
import com.proliferate.Proliferate.config.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    private String contactNumber;

    @StrongPassword
    @JsonIgnore
    private String password;

    private Gender gender;

    private int age;

}
