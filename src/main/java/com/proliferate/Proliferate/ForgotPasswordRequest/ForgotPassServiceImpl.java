package com.proliferate.Proliferate.ForgotPasswordRequest;


import com.proliferate.Proliferate.Domain.DTO.ResetPassword;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorVerification;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.ExeceptionHandler.EmailNotFoundException;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForgotPassServiceImpl implements ForgotPassTokenService{

    private final ForgotPassTokenRep forgotPassTokenRep;

    private final EmailService emailService;

    private final StudentRepository studentRepository;

    private final TutorRepository tutorRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final int MINS = 5;



    @Transactional
    @Override
    public void initiateForgotPass(TutorVerification tutorVerification) {
        try {
            UserDetails existingUser = null;
            Optional<StudentEntity> studentOpt = studentRepository.findByEmail(tutorVerification.getEmail());
            if (studentOpt.isPresent()) {
                existingUser = studentOpt.get();
            } else {
                Optional<TutorEntity> tutorOpt = tutorRepository.findByEmail(tutorVerification.getEmail());
                if (tutorOpt.isPresent()) {
                    existingUser = tutorOpt.get();
                }
            }

            if (existingUser == null) {
                throw new EmailNotFoundException("User with email " + tutorVerification.getEmail() + " not found");
            }

            ForgotPassToken forgotPassToken = new ForgotPassToken();
            forgotPassToken.setExpireTime(expireTimeRange());
            forgotPassToken.setToken(generateOTP());
            forgotPassToken.setUser(existingUser);
            forgotPassToken.setUsed(false);

            forgotPassTokenRep.save(forgotPassToken);
            emailService.sendPasswordMail(getEmail(existingUser), getFirstName(existingUser), forgotPassToken.getToken());
        } catch (Exception e) {
            throw new RuntimeException("Encountered an error while saving token", e);
        }
   }


    @Transactional
    @Override
    public ResponseEntity<String> resetPassword(ResetPassword resetPassword) {
        ForgotPassToken forgotPassToken = forgotPassTokenRep.findByToken(resetPassword.getToken());
        if (forgotPassToken == null || !forgotPassToken.getToken().equals(resetPassword.getToken())) {
            return ResponseEntity.badRequest().body("Token does not match");
        }

        UserDetails userEntity = forgotPassToken.getUser();
        if (userEntity == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        checkValidity(forgotPassToken);

        if (userEntity instanceof StudentEntity) {
            StudentEntity student = (StudentEntity) userEntity;
            if (!student.isRegistrationCompleted()) {
                return ResponseEntity.badRequest().body("Registration is not completed for this student");
            }
            student.setPassword(passwordEncoder.encode(resetPassword.getNewPassword()));
            studentRepository.save(student);
        } else if (userEntity instanceof TutorEntity) {
            TutorEntity tutor = (TutorEntity) userEntity;
            if (!tutor.isRegistrationCompleted()) {
                return ResponseEntity.badRequest().body("Registration is not completed for this tutor");
            }
            tutor.setPassword(passwordEncoder.encode(resetPassword.getNewPassword()));
            tutorRepository.save(tutor);
        }

        forgotPassToken.setUsed(true);
        forgotPassTokenRep.save(forgotPassToken);

        return ResponseEntity.ok("Password reset successful.");
    }
	
	
	@Scheduled(cron = "0 0 * * * *") // This cron expression runs every hour
    public void clearTokenAfterExpiration() {
        List<ForgotPassToken> forgotPassTokens = forgotPassTokenRep.findAll();
        LocalDateTime now = LocalDateTime.now();
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (ForgotPassToken forgotPassToken : forgotPassTokens) {
            LocalDateTime tokenExpire = forgotPassToken.getExpireTime();
            if (now.isAfter(tokenExpire.plusHours(1))) {
                forgotPassTokenRep.delete(forgotPassToken);
            }
        }
    }

    public LocalDateTime expireTimeRange(){
        return LocalDateTime.now().plusMinutes(MINS);
    }

    public String generateOTP(){
        List rules = Arrays.asList(new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1));

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generatePassword(6,rules);
        return password;
    }

    public boolean isExpired(ForgotPassToken forgotPassToken){
        return LocalDateTime.now().isAfter(forgotPassToken.getExpireTime());
    }

    public ResponseEntity<String> checkValidity(ForgotPassToken forgotPassToken){
        if (forgotPassToken == null){
            return ResponseEntity.badRequest().body("token not found");
        }
        else  if (forgotPassToken.isUsed()){
            return ResponseEntity.badRequest().body("token already used");
        } else if (isExpired(forgotPassToken)) {
            return ResponseEntity.badRequest().body("token is expired");
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
	
	    private String getFirstName(UserDetails user) {
        if (user instanceof StudentEntity) {
            return ((StudentEntity) user).getFirstName();
        } else if (user instanceof TutorEntity) {
            return ((TutorEntity) user).getFirstName();
        }
        return "";
    }

    private String getEmail(UserDetails user) {
        if (user instanceof StudentEntity) {
            return ((StudentEntity) user).getEmail();
        } else if (user instanceof TutorEntity) {
            return ((TutorEntity) user).getEmail();
        }
        return "";
    }
}
