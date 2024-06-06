package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateTutor {
    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String bio;

    private MultipartFile studentImage;
}