package com.proliferate.Proliferate.ForgotPasswordRequest;


import com.proliferate.Proliferate.Domain.Entities.UserEntity;
import com.proliferate.Proliferate.Repository.UserRepository;
import com.proliferate.Proliferate.Service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForgotPassServiceImpl implements ForgotPassTokenService{

    private final ForgotPassTokenRep forgotPassTokenRep;

    private final EmailService emailService;

    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final int MINS = 5;


    @Transactional
    @Override
    public void initiateForgotPass(String email) {
        try {
            UserEntity existingUser = userRepository.findByEmail(email).orElse(null);
            if (existingUser == null) {
                throw new RuntimeException("User with email " + email + " not found");
            }
            ForgotPassToken forgotPassToken = new ForgotPassToken();

            forgotPassToken.setExpireTime(expireTimeRange());
            forgotPassToken.setToken(generateOTP());
            forgotPassToken.setUser(existingUser);
            forgotPassToken.setUsed(false);
            forgotPassTokenRep.save(forgotPassToken);
            emailService.sendPasswordMail(existingUser.getEmail(), existingUser.getFirstName(), forgotPassToken.getToken());
        } catch (Exception e) {
            throw new RuntimeException("Encountered an error while saving token", e);
        }
    }

@Transactional
@Override
public ResponseEntity<String> resetPassword(String token, String newPassword) {
    ForgotPassToken forgotPassToken = forgotPassTokenRep.findByToken(token);
    if (forgotPassToken == null || !forgotPassToken.getToken().equals(token)) {
        return ResponseEntity.badRequest().body("Token does not match");
    }

    UserEntity userEntity = forgotPassToken.getUser();
    if (userEntity != null) {
        checkValidity(forgotPassToken);
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        forgotPassToken.setUsed(true);
        userRepository.save(userEntity);
        forgotPassTokenRep.save(forgotPassToken);
        return ResponseEntity.ok("Password Reset successful.");
    } else {
        return ResponseEntity.badRequest().body("User not found");
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

}
