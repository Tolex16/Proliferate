package com.proliferate.Proliferate.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginResponse {

    @JsonProperty("token")
    private final String token;
	
	private final Boolean hasBio;

}
