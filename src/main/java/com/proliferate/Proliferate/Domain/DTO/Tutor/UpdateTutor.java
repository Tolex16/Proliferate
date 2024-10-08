package com.proliferate.Proliferate.Domain.DTO.Tutor;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateTutor {

    private String bio;

    private MultipartFile tutorImage;
}