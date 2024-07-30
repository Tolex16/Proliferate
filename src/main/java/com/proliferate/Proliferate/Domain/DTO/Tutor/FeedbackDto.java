package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FeedbackDto {
    private Long tutorId;

    private Long subjectId;

    private String studentName;

    private String sessionDate;

    private int rating;

    private String comments;
}
