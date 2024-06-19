package com.proliferate.Proliferate.Domain.DTO.Student;

import com.proliferate.Proliferate.Domain.Entities.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ScoreDto {

    private Long studentId;

    private Long testId;

    private int marks;

    private int questionsAttempted;

    private int correctAnswers;

    private int wrongAnswers;

    private Result result;
}
