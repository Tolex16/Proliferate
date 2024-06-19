package com.proliferate.Proliferate.Domain.DTO.Student;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateStudent {
    private String firstName;

    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    private String phoneNumber;

    private String bio;

    private MultipartFile studentImage;
}
