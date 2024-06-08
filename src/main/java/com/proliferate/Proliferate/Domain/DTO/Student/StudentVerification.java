package com.proliferate.Proliferate.Domain.DTO.Student;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentVerification {

    @JsonProperty("userName")
    private String userName;

    @Email(message = "Input a real email address")
    @JsonProperty("email")
    private String email;
}
