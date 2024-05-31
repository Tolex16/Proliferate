package com.proliferate.Proliferate.Domain.DTO.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Feedback {

    private String tutorName;

    private String subject;

    private String sessionDate;

    private String rating;

    private String comments;
}
