package com.proliferate.Proliferate.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginResponse {

    private final StudentDto studentDto;
	
	private final TutorDto tutorDto;

    @JsonProperty("token")
    private final String token;

}
