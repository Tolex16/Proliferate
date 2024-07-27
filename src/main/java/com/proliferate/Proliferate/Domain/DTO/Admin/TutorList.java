package com.proliferate.Proliferate.Domain.DTO.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TutorList {
    private long tutorId;

    private String fullName;

    private String email;

    private String availability;

    private String bio;

}
