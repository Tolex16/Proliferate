package com.proliferate.Proliferate.Domain.DTO.Student;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SessionDto {
    private Long sessionId;

    private Long subjectId;

    private Long tutorId;

    private Long studentId;
	@NotNull
	private int frequency;

    @NotNull
    private String duration;
}
