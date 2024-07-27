package com.proliferate.Proliferate.Domain.DTO.Student;

import jakarta.persistence.Column;
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
	
	private int frequency;

    private int duration;
}
