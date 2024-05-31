package com.proliferate.Proliferate.Domain.DTO.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LearningGoals {

    private String shortTermGoals;

    private String longTermGoals;
}
