package com.proliferate.Proliferate.Domain.DTO.Tutor;

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
public class TutorRegister {

    private long tutorId;

    private String firstName;

    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    private String contactNumber;

    @StrongPassword
    private String password;

    private Gender gender;

    private int age;
}