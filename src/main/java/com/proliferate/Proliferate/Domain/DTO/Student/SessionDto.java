package com.proliferate.Proliferate.Domain.DTO.Student;

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
}
