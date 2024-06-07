package com.proliferate.Proliferate.Domain.DTO.Student;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernameVerification {

    @JsonProperty("userName")
    private String userName;
}
