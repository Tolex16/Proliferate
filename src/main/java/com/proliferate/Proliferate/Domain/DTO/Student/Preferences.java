package com.proliferate.Proliferate.Domain.DTO.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Preferences {

    private String availability;

    private String additionalPreferences;

    private String requirements;

    private String communicationLanguage;
}
