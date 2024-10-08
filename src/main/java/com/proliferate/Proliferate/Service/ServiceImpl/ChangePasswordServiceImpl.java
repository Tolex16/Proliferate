package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.*;
import com.proliferate.Proliferate.Domain.DTO.Student.*;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorDto;
import com.proliferate.Proliferate.Domain.Entities.AdminEntity;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.*;
import com.proliferate.Proliferate.Repository.AdminRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;

import com.proliferate.Proliferate.Service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChangePasswordServiceImpl implements ChangePasswordService {
    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final StudentRepository studentRepository;
	private final TutorRepository tutorRepository;

    private final AdminRepository adminRepository;

    public ResponseEntity<?> changeStudentPassword(StudentEntity student, ChangePasswordRequest request) {
        if (request.getCurrentPassword() == null || request.getNewPassword() == null || request.getConfirmNewPassword() == null) {
            throw new InvalidPasswordException("Passwords cannot be null");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), student.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new SamePasswordException("New password and confirm new password do not match");
        }

        student.setPassword(passwordEncoder.encode(request.getNewPassword()));
        studentRepository.save(student);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> changeTutorPassword(TutorEntity tutor, ChangePasswordRequest request) {
        if (request.getCurrentPassword() == null || request.getNewPassword() == null || request.getConfirmNewPassword() == null) {
            throw new InvalidPasswordException("Passwords cannot be null");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), tutor.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new SamePasswordException("New password and confirm new password do not match");
        }

        tutor.setPassword(passwordEncoder.encode(request.getNewPassword()));
        tutorRepository.save(tutor);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> changeAdminPassword(AdminEntity admin, ChangePasswordRequest request) {
        if (request.getCurrentPassword() == null || request.getNewPassword() == null || request.getConfirmNewPassword() == null) {
            throw new InvalidPasswordException("Passwords cannot be null");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new SamePasswordException("New password and confirm new password do not match");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminRepository.save(admin);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

