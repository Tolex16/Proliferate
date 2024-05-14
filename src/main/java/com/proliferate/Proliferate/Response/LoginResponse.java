package com.proliferate.Proliferate.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proliferate.Proliferate.Domain.DTO.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {

    private final UserDto userDto;

    @JsonProperty("token")
    private final String  token;

}
