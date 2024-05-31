package com.proliferate.Proliferate.Domain.DTO.Tutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UploadDocuments {
    private MultipartFile educationalCertificates;
	
    private MultipartFile resumeCurriculumVitae;
	
    private MultipartFile professionalDevelopmentCert;
	
    private MultipartFile identificationDocuments;
}
